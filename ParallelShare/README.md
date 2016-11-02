How to build and run

1. Put your keystore file in /ParallelShare/keystore folder.
2. Modify default signing sonfig in /ParallelShare/app/build.gradle with your own key alias and password.

FACEBOOK

3. Create a Facebook app. https://developers.facebook.com/quickstarts/?platform=android
4. Get the app id and replace it in strings.xml file
	<string name="facebook_app_id">xxxx</string>
    <string name="fb_login_protocol_scheme">fbxxxx</string>

5. Generate key hash for your signing certs and add it to app information in dev console.
	keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64

TWITTER

6. Create a Twitter app. https://dev.twitter.com/
7. Declare the consumer_key and consumer_secret in gradle.properties file.
TwitterKey="xxxxxx"
TwitterSecret="xxxxx"
#in your home folder /.gradle/gradle.properties (create gradle.properties if it does not exist)

8. Specify the callback host and scheme in strings.xml file.
	callback url is of the form scheme://host (Example oauth://myapp)
    <string name="twitter_callback_scheme">oauth</string>
    <string name="twitter_callback_host">myapp</string>


PLAY SERVICES

9. Create a new project in Firebase, add app and add Admobs. (Or create app in admobs and link to firebase project).
		https://console.firebase.google.com/
10. Download the google-services.json and put in /app folder.
		/ParallelShare/app/google-services.json
11. Add banner id and app id from admobs into strings.xml.
	<string name="banner_ad_unit_id">ca-app-pub-5670593069297116/45241506xx</string>
    <string name="app_ad_id">ca-app-pub-5670593069297116~30474174xx</string>
11. Analytics will automatically report to firebase project linked with corresponding application id. No need to dynamically set up.
