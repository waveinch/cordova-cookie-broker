(function () {
  'use strict';
  /*global cordova, module*/
  var exec = cordova.require('cordova/exec');
  var cookieBroker = {};
  cookieBroker.getCookies = getCookies;

  function getCookies(url) {
    return new Promise(function (resolve, reject) {
      cordova.exec(successCallback, errorCallback, "CookieBroker", "getCookies", [url]);

      function successCallback(cookieList) {
        resolve(cookieList);
      }

      function errorCallback(response) {
        reject(response);
      }
    });


  };

  module.exports = cookieBroker.getCookies;
})();