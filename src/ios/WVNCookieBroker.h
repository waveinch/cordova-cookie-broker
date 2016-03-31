#import <Cordova/CDV.h>

@interface WVNCookieBroker : CDVPlugin

- (void) getCookies:(CDVInvokedUrlCommand*)command;

@end