##To run react project build for our testing purposes:

* Add app registrations in Azure for both client (Native app type) and backend (Web api)
* Edit `adal/adal-config.js` and change `tenant`, `clientId` and `endpoints`
* run `npm i` to install dependencies
* run `npm run build`, the built app will be placed into `build` folder, you can serve it later from `/src/main/resources/react/build`

P.S. Backend api call is yet to be checked, for now I just implemented the simplest case when the entire app is inaccessible until user logs in. 
