cordova.define("cordova-cookie-broker.CookieBroker", function (require, exports, module) {
  (function () {
    'use strict';
    /*global cordova, module*/
    var exec = cordova.require('cordova/exec');
    var cookieBroker = {};
    cookieBroker.getCookies = getCookies;

    function getCookies(url, opts) {
      return new Promise(function (resolve, reject) {
        cordova.exec(successCallback, errorCallback, "CookieBroker", "getCookies", [url, opts]);

        function successCallback(cookieList) {
          resolve(cookieList);
        }

        function errorCallback(response) {
          reject(response);
        }
      });


    };

    module.exports = cookieBroker;
  })();
});