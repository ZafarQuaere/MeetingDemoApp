properties(
    [
        [$class: "JiraProjectProperty"],
        [$class: "RebuildSettings", autoRebuild: false, rebuildDisabled: false],
        parameters(
            [
                choice(choices: ["GM", "LUMEN", "GMANDLUMEN"], description: "Select a product build", name: "BUILD_PRODUCT"),
                choice(choices: ["Prod", "Qac", "Qab"], description: "Select a env to build", name: "ENV"),
                choice(choices: ["Default", "Major", "Minor", "Patch"], description: "Select a Semver increment argument", name: "INCREMENT_LEVEL"),
            ])
    ]
)
def OUR_DOCKER = "docker.artifactory.pgi-tools.com"
def BUILD_ID = env.BUILD_NUMBER.replace("_", "-").replace("/", "-")
def POD_LABEL = "buildpod-${env.JOB_NAME.take(25)}-" + BUILD_ID
def BUILD_IMAGE = "${OUR_DOCKER}/helpers/android-sdk:master-58-d4d024d"
def BUILD_GM = false
def BUILD_LUMEN = false
def BUILD_ENV = params.ENV
if (params.BUILD_PRODUCT == "GM") {
  BUILD_GM = true
} else if (params.BUILD_PRODUCT == "LUMEN") {
  BUILD_LUMEN = true
} else {
    BUILD_GM = true
    BUILD_LUMEN = true
}

@Library("Utility-Pipeline")
def pipeline = new com.pgi.jenkins.Pipeline()
def roomName = "#mobstars-builds"
pipeline.setNotificationRoom(roomName)

podTemplate(label: POD_LABEL, yaml: """
apiVersion: v1
kind: Pod
spec:
  nodeSelector:
    kops.k8s.io/instancegroup: android_nodes
  containers:
  - name: android-sdk
    image: '$BUILD_IMAGE'
    alwaysPullImage: false
    tty: true
    resources:
      requests:
        cpu: 2
        memory: 13Gi
      limits:
        cpu: 4
        memory: 13Gi
    command:
    - cat
  - name: jnlp
    image: jenkins/jnlp-slave
    securityContext:
      runAsUser: 10000
      allowPrivilegeEscalation: false
    args: ['\$(JENKINS_SECRET)', '\$(JENKINS_NAME)']
    tty: true
  imagePullSecrets:
  - name: artifactory-pgitools
"""
) {
    pipeline.sendNotifications ("STARTED", BRANCH_NAME, "")
    node(POD_LABEL) {
        container("android-sdk") {
            try {
                stage ("Checkout"){
                    scmInfo = checkout scm
                }
            } catch(e) {
                pipeline.sendNotifications("FAILURE", BRANCH_NAME, "CHECKOUT")
                currentBuild.rawBuild.result = Result.FAILURE
                throw e
            }
            try {
                if(BUILD_GM) {
                    build("GM", BUILD_ENV, BUILD_ENV, "AndroidSigningKeystore", "AndroidSigningKeystore", "GooglePlayJSONKey", "com.pgi.gmmeet")
                }
                if(BUILD_LUMEN) {
                    def buildFlavor= "lumen"
                    if(BUILD_ENV == "Prod") {
                        buildFlavor = "Lumen"
                    } else if(BUILD_ENV == "Qab") {
                        buildFlavor = "Lumenqab"
                    } else if(BUILD_ENV == "Qac") {
                        buildFlavor = "Lumenqac"
                    }
                    build("LUMEN", BUILD_ENV, buildFlavor, "CTLAndroidSigningKeystore", "centurylinkworkplace", "CTLGooglePlayJSONKey", "com.centurylink.ctlworkplace")
                }
            } catch(e) {
                pipeline.sendNotifications("FAILURE", BRANCH_NAME, "BUILD")
                currentBuild.rawBuild.result = Result.FAILURE
                throw e
            }
        }
    }
    pipeline.sendNotifications("SUCCESS", BRANCH_NAME, "")
}

def build(product, buildEnv, flavor, credentialsId, keyAlias, fileId, packageName) {
    def GIT_USER_EMAIL = "saasops@pgi.com"
    def GIT_USER_NAME = "Jenkins Build Server"
    def SONAR_TARGET = env.BRANCH_NAME.matches("^(develop)") ? "" : "develop";
    def IS_PR = env.BRANCH_NAME.startsWith("PR-")
    def BUILD_LEVEL = params.INCREMENT_LEVEL.toLowerCase()
    def STAGE_NAME = ""
    def FASTLANE_CMD_LINE_ARGS = ""
    def FASTLANE_CERTS_CMD_LINE_ARGS = ""
    try {
        STAGE_NAME = "Setup Build Env"
        stage(STAGE_NAME) {
            dir ("$WORKSPACE/convergencemeetings") {
                withCredentials(
                    [
                        usernamePassword(
                            credentialsId: "artifactory-pgitools-publisher",
                            usernameVariable: "ARTIFACTORY_USERNAME",
                            passwordVariable: "ARTIFACTORY_PASSWORD"
                        ),
                        usernamePassword(
                            credentialsId: "jenkins-pgitools", 
                            usernameVariable: "BITBUCKET_USERNAME", 
                            passwordVariable: "BITBUCKET_PASSWORD"
                        )
                    ]
                ) {
                    def BITBUCKET_USER_NAME = BITBUCKET_USERNAME.replace("@","%40")
                    def SONAR_TARGET_BRANCH = SONAR_TARGET
                    if(IS_PR) {
                        SONAR_TARGET_BRANCH = CHANGE_TARGET
                    } 
                    def artifactoryserver = Artifactory.server "artifactory"
                    def GIT_COMMIT_ID = sh(returnStdout: true, script: "git rev-parse HEAD").trim()
                        FASTLANE_CMD_LINE_ARGS =  "buildId:\"${env.BUILD_ID}\" buildLevel:\"${BUILD_LEVEL}\" buildEnv:\"${buildEnv}\" \
                        bitbucketUserName:\"${BITBUCKET_USER_NAME}\" bitbucketPwd:\"${BITBUCKET_PASSWORD}\" \
                        artifactoryUserName:\"${ARTIFACTORY_USERNAME}\" artifactoryPwd:\"${ARTIFACTORY_PASSWORD}\" artifactoryServer:\"${artifactoryserver.url}\" \
                        flavor:\"${flavor}\" product:\"${product}\" \
                        sonarTarget:\"${SONAR_TARGET_BRANCH}\" branchName:\"${env.BRANCH_NAME}\" commitId:\"${GIT_COMMIT_ID}\""
                }
                 sh """ git config --global user.email \"${GIT_USER_EMAIL}\" 
                        git config --global user.name \"${GIT_USER_NAME}\"
                    """
            }
        }
    } catch(e) {
        echo "Setup Build Env failed"
        throw e
    }
    try {
        stage("Quality Check") {
            dir ("$WORKSPACE/convergencemeetings") {
                withSonarQubeEnv("SonarQube") {
                    withGradle {
                        sh "chmod +x gradlew"
                        sh """fastlane runSonar buildType:Debug ${FASTLANE_CMD_LINE_ARGS}"""
                    }
                }
            }
        }
    } catch(e) {
        echo "Quality Check failed"
        throw e
    } 
   try {
        stage("Quality Gate") {
            timeout(time: 20, unit: "MINUTES") {
                def qg = waitForQualityGate()
                if (qg.status != "OK") {
                    pipeline.sendNotifications("FAILURE", "QUALITY GATE", BRANCH_NAME)
                    error "Pipeline aborted due to quality gate failure: ${qg.status}"
                    currentBuild.rawBuild.result = Result.FAILURE
                }
            }
        }
    } catch(e) {
        echo "Quality Gate failed"
        throw new hudson.AbortException("SONAR Quality Gate FAILURE")
    }
    try {
        stage ("Build And Deploy"){
            dir ("$WORKSPACE/convergencemeetings") {
                withCredentials(
                    [
                        certificate(
                            credentialsId: credentialsId, 
                            keystoreVariable: "CERTPATH", 
                            passwordVariable: "KEYSTOREPASSWORD"
                        )
                    ]
                ) {
                  configFileProvider([configFile(fileId: fileId, variable: 'GOOGLE_KEY')]) {
                    FASTLANE_CERTS_CMD_LINE_ARGS = """keystorePath:\"${CERTPATH}\" keystoreAlias:\"${keyAlias}\" keystorePassword:\"${KEYSTOREPASSWORD}\" keystoreAliasPassword:\"${KEYSTOREPASSWORD}\" \
                        googleKey:\"${GOOGLE_KEY}\" packageName:\"${packageName}\""""
                    sh """fastlane buildAndDeploy buildType:Release ${FASTLANE_CMD_LINE_ARGS} ${FASTLANE_CERTS_CMD_LINE_ARGS}"""
                  }
                }
            }
        }
    } catch(e) {
        echo "Build And Deploy failed"
        throw e
    }
}
