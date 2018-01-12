// enables verbose logging to console
/*window.Logging = {
    level: 3,
    log: function (message) {
        console.log(message);
    }
};*/

const adalConfig = {
    tenant: 'stanislavChernov.onmicrosoft.com',
    clientId: '5be39bef-6ca2-4df9-9bf0-2e58fa7c6103',// '623e3b66-cafa-4107-b290-204c39361546',
    extraQueryParameter: 'nux=1',
    disableRenewal: true,
    endpoints: {
        api: '5be39bef-6ca2-4df9-9bf0-2e58fa7c6103' // '[app Id or registered in Azure BACKEND api app]'
    },
    // postLogoutRedirectUri: window.location.origin, // endpoint to redirect to after logout, defaults to redirectUri
    redirectUri: window.location.origin,
    cacheLocation: 'sessionStorage'
};

export default adalConfig;