package com.pgi.convergence.utils;

object FeaturesUtil {
	fun checkLaunchUrlDomainMatchesPackage(flavor: String, link: String): Boolean {
		return if(flavor.toLowerCase().contains("lumen")) {
			link.contains("yourlumenworkplace",true)
		} else {
			link.contains("globalmeet",true)
		}
	}
}
