# Privacy Policy

The partyq app does not keep or store any of your Spotify data, or any other personal data. It is only used within the context of the app to perform the necessary functions and is immediately released when no longer in use.

### Spotify Data

* When the host authenticates with Spotify while starting a party, partyq gets back an OAuth token that is used for authenticating search requests and checking if the user has Spotify premium. This token is never shared with any other user of the app, and is erased when the host ends the party or when the host fully closes the app (whichever happens first).
* Guests don't have any Spotify data (that partyq is aware of, anyway) and do not gain access to any by connecting to the host.

The code for handling the host's OAuth token is located in the [TokenHandlerService](https://github.com/niehusst/partyq/blob/main/app/src/main/java/com/niehusst/partyq/services/TokenHandlerService.kt).

### Other User Data

partyq saves the following data in a local file only accessible by the partyq app: 
* a host's Spotify OAuth token (only while a party is occurring)
* when the token expires
* the 4 digit code of the current/last party the user joined
* whether the user is the host of the current party
* whether the user has just joined the party

partyq uses a number of permissions, all with the sole purpose of connecting you to the party host/your guests.
* Bluetooth permissions: so partyq can create a Bluetooth connection between devices for exchanging party data
* Wifi permissions: so partyq can create a Wifi connection between devices for exchanging party data
* Fine and Coarse location permissions: partyq does NOT use or collect user location data in any way. This permission is required for making Bluetooth and Wifi connections. 

No other user data is recorded or maintained, and none of it is shared with anyone.