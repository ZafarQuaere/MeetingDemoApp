fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew cask install fastlane`

# Available Actions
## Android
### android runSonar
```
fastlane android runSonar
```
Run tests and Branch analysis
### android runPRSonar
```
fastlane android runPRSonar
```
Run tests and PR analysis
### android buildAndDeploy
```
fastlane android buildAndDeploy
```
Build and deploy app
### android bundle
```
fastlane android bundle
```
Bundle app
### android verify
```
fastlane android verify
```
Verify next release
### android signAndExtract
```
fastlane android signAndExtract
```
Sign and extract
### android upload_to_artifactory
```
fastlane android upload_to_artifactory
```
Upload to artifactory
### android post_deploy
```
fastlane android post_deploy
```
Generates release notes for slack and create the next tag

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
