#import <Cordova/CDVPlugin.h>

@interface WVNCookieBroker : CDVPlugin

- (void) getCookies:(CDVInvokedUrlCommand*)command;

@end