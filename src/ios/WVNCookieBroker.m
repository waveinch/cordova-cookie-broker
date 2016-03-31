#import "WVNCookieBroker.h"
#import <Cordova/CDVPlugin.h>

@implementation WVNCookieBroker

- (void)getCookies:(CDVInvokedUrlCommand*)command
{

    NSString* callbackId = [command callbackId];
    NSString* url = [[command arguments] objectAtIndex:0];
    
    
    id opts = [[command arguments] objectAtIndex:1];
    
    NSString* cookieString = [self extractHeaderString:opts];
    
    [self.commandDelegate runInBackground:^{
        NSMutableURLRequest *request =
        [NSMutableURLRequest requestWithURL:[NSURL
                                             URLWithString:url]
                                cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData
                            timeoutInterval:10
         ];
        
        [request setHTTPMethod: @"GET"];
        
        if (![cookieString isEqual: @""]) {
            [request setValue:cookieString forHTTPHeaderField:@"Cookie"];
        }
        
        
        NSURLSession *session = [NSURLSession sharedSession];
        [[session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
            NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse*)response;
            if ([response respondsToSelector:@selector(allHeaderFields)]) {
                NSArray *cookies =[[NSArray alloc]init];
                cookies = [NSHTTPCookie
                           cookiesWithResponseHeaderFields:[httpResponse allHeaderFields]
                           forURL:@""];
                NSLog(cookies.description);
                CDVPluginResult* result = [CDVPluginResult
                                           resultWithStatus:CDVCommandStatus_OK
                                           messageAsArray:cookies];
                
                [self.commandDelegate sendPluginResult:result callbackId:callbackId];
            }
           
            
            
        }] resume];
        
        
        
    }];
    
}

-(NSString*) extractHeaderString: (id)opts {
    NSString* cookieStringKey = @"cookieHeader";
    NSString* cookieString = @"";
    if (opts[cookieStringKey] != nil && [opts[cookieStringKey] isKindOfClass:[NSString class]]) {
        cookieString = opts[cookieStringKey];
    }
    
    return cookieString;
}

@end