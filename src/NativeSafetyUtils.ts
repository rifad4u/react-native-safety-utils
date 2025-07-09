import type { TurboModule } from "react-native";
import { TurboModuleRegistry } from "react-native";

export interface Spec extends TurboModule {
    isVpnActive: () => Promise<boolean>;
    isProxyEnabled: () => Promise<boolean>;
    isMockedLocation: () => Promise<boolean>;
    isLocationEnabled: () => Promise<boolean>;
    isScreenMirroring: () => Promise<boolean>;
    isDeveloperOptionsEnabled: () => Promise<boolean>;
    isAppTampered: (signature: string) => Promise<boolean>;
    calculateAPKChecksum: (algorithm: string) => Promise<string | null>;
};

export default TurboModuleRegistry.getEnforcing<Spec>("SafetyUtils");