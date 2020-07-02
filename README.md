allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	
	
	dependencies {
	        implementation 'com.github.a7pzero:PiracyChecker:Tag'
	}
	
	
	
	Import this into mainactivity
	
	import com.github.a7pzero.piracychecker.PiracyChecker;
        import com.github.a7pzero.piracychecker.enums.PirateApp;
	
	
	
	Add this after oncreate function in main activity 
	
	
	 PirateApp pirateApp = new PirateApp("APP_NAME", "Pakage_name");
       
        new PiracyChecker(getActivity()).addAppToCheck(pirateApp).enableUnauthorizedAppsCheck().start();

