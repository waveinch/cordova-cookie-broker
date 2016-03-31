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
                           forURL:[NSURL URLWithString:@""]];
                NSMutableArray* jsonCookies = [[NSMutableArray alloc] init];
                for (NSHTTPCookie* cookie in cookies) {
                    [jsonCookies addObject:[self cookieJSON:cookie]];
                }
                
                
                CDVPluginResult* result = [CDVPluginResult
                                           resultWithStatus:CDVCommandStatus_OK
                                           messageAsArray:jsonCookies];
                
                
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

-(NSMutableDictionary *) cookieJSON: (NSHTTPCookie *) cookie {
    NSMutableDictionary *result = [NSMutableDictionary dictionary];
    if ([cookie name] != nil){
        [result setObject:[cookie name] forKey:@"name"];
    }
    if ([cookie value] != nil){
        [result setObject:[cookie value] forKey:@"value"];
    }
    if ([cookie domain] != nil){
        [result setObject:[cookie domain] forKey:@"domain"];
    }
    if ([cookie path] != nil){
        [result setObject:[cookie path] forKey:@"path"];
    }
    if ([cookie expiresDate] != nil){
        NSNumber * dateMillis =[NSNumber numberWithDouble:[[cookie expiresDate] timeIntervalSince1970] * 1000];
        [result setObject:dateMillis forKey:@"maxAge"];
    }
    return result;
}

@end