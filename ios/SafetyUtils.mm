#import "SafetyUtils.h"

#import <SystemConfiguration/SystemConfiguration.h>
#import <CFNetwork/CFNetwork.h>
#import <NetworkExtension/NetworkExtension.h>
#import <CoreLocation/CoreLocation.h>

@implementation SafetyUtils

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(calculateAPKChecksum:(nonnull NSString *)algorithm
                resolve:(nonnull RCTPromiseResolveBlock)resolve
                reject:(nonnull RCTPromiseRejectBlock)reject) {

    resolve(@"");

}

RCT_EXPORT_METHOD(isAppTampered:(nonnull NSString *)expectedBundleID
            resolve:(nonnull RCTPromiseResolveBlock)resolve
            reject:(nonnull RCTPromiseRejectBlock)reject) {

    NSString *currentBundleID = [[NSBundle mainBundle] bundleIdentifier];

    if (![currentBundleID isEqualToString:expectedBundleID]) {
        // NSLog(@"⚠️ App Spoofing Detected: Bundle ID Mismatch!");
        resolve(@(YES));
        return;
    }

    NSString *provisioningPath = [[NSBundle mainBundle] pathForResource:@"embedded" ofType:@"mobileprovision"];
    if (!provisioningPath) {
        // NSLog(@"⚠️ App is not properly signed. Possible modification detected!");
        resolve(@(YES));
        return;
    }

    // NSLog(@"✅ App is authentic and properly signed.");
    resolve(@(NO));

}

RCT_EXPORT_METHOD(isDeveloperOptionsEnabled:(nonnull RCTPromiseResolveBlock)resolve
                        reject:(nonnull RCTPromiseRejectBlock)reject) {
    resolve(@(NO));
}

RCT_EXPORT_METHOD(isLocationEnabled:(nonnull RCTPromiseResolveBlock)resolve
                reject:(nonnull RCTPromiseRejectBlock)reject) {
    resolve(@(YES));
}

RCT_EXPORT_METHOD(isMockedLocation:(nonnull RCTPromiseResolveBlock)resolve
                reject:(nonnull RCTPromiseRejectBlock)reject) {
    
    CLLocationManager *locationManager;
    locationManager = [[CLLocationManager alloc] init];
    CLLocation *location = locationManager.location;

    if (location && location.sourceInformation) {
        BOOL isSimulated = location.sourceInformation.isSimulatedBySoftware;
        BOOL isProducedByAccessory = location.sourceInformation.isProducedByAccessory;
        BOOL isFake = isSimulated || isProducedByAccessory;
        resolve(@(isFake));  // Returns YES if fake location is detected, NO otherwise
    } else {
        resolve(@(NO)); // No location data available (e.g., permissions denied)
    }
    
}

RCT_EXPORT_METHOD(isProxyEnabled:(nonnull RCTPromiseResolveBlock)resolve
                reject:(nonnull RCTPromiseRejectBlock)reject) {
    
    CFDictionaryRef proxySettingsRef = CFNetworkCopySystemProxySettings();
        
    if (!proxySettingsRef) {
        resolve(@(NO)); // Return false if no settings found
        return;
    }
        
    NSDictionary *proxySettings = (__bridge_transfer NSDictionary *)proxySettingsRef;
    BOOL isProxyEnabled = NO;

    if ([proxySettings[(NSString *)kCFNetworkProxiesHTTPEnable] boolValue]) {
        NSString *httpProxyHost = proxySettings[(NSString *)kCFNetworkProxiesHTTPProxy];
        if (httpProxyHost != nil && ![httpProxyHost isEqualToString:@""]) {
            isProxyEnabled = YES;
        }
    }

    resolve(@(isProxyEnabled));

}

RCT_EXPORT_METHOD(isScreenMirroring:(nonnull RCTPromiseResolveBlock)resolve
                    reject:(nonnull RCTPromiseRejectBlock)reject) {
    
    if ([UIScreen screens].count > 1) {
        resolve(@(YES));
        return;
    }
    resolve(@(NO));

}

RCT_EXPORT_METHOD(isVpnActive:(nonnull RCTPromiseResolveBlock)resolve
            reject:(nonnull RCTPromiseRejectBlock)reject) {

    NSDictionary *proxySettings = (__bridge NSDictionary *)(CFNetworkCopySystemProxySettings());
    NSDictionary *scoped = proxySettings[@"__SCOPED__"];

    for (NSString *key in scoped.allKeys) {
        if ([key containsString:@"tap"] ||
            [key containsString:@"tun"] ||
            [key containsString:@"ppp"] ||
            [key containsString:@"ipsec"] ||
            [key containsString:@"utun"]) {
            resolve(@(YES));
            return;
        }
    }
    
    resolve(@(NO));

}

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

#if RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeSafetyUtilsSpecJSI>(params);
}
#endif

@end
