#ifdef RCT_NEW_ARCH_ENABLED
#import <SafetyUtilsSpec/SafetyUtilsSpec.h>

@interface SafetyUtils : NSObject <NativeSafetyUtilsSpec>
#else
#import <React/RCTBridgeModule.h>

@interface SafetyUtils : NSObject <RCTBridgeModule>
#endif

@end
