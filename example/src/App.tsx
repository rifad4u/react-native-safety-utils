import { useState } from "react";
import { View, StyleSheet, Button, Text, ScrollView, Platform } from "react-native";
import SafetyUtils from "react-native-safety-utils";
import { request, PERMISSIONS, RESULTS } from "react-native-permissions";

export default function App() {

    const [isDevOptionsEnabled, setIsDevOptionsEnabled] = useState<string>();
    const [isScreenMirroring, setIsScreenMirroring] = useState<string>();
    const [isVpnActive, setIsVPNActive] = useState<string>();
    const [isProxyEnabled, setIsProxyEnabled] = useState<string>();
    const [isLocationEnabled, setIsLocationEnabled] = useState<string>();
    const [isMockedLocation, setIsMockedLocation] = useState<string>();

    const checkDevOptions = async () => {
        const isEnabled = await SafetyUtils.isDeveloperOptionsEnabled();
        setIsDevOptionsEnabled(String(isEnabled));
    };

    const checkScreenMirroring = async () => {
        const isEnabled = await SafetyUtils.isScreenMirroring();
        setIsScreenMirroring(String(isEnabled));
    };

    const checkVpn = async () => {
        const isEnabled = await SafetyUtils.isVpnActive();
        setIsVPNActive(String(isEnabled));
    };

    const checkProxy = async () => {
        const isEnabled = await SafetyUtils.isProxyEnabled();
        setIsProxyEnabled(String(isEnabled));
    };

    const checkPermission = async () => {
        const permission = Platform.OS === "ios" ? PERMISSIONS.IOS.LOCATION_WHEN_IN_USE : PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION;
        return request(permission);
    };

    const checkLocationEnabled = async () => {
        const isEnabled = await SafetyUtils.isLocationEnabled();
        setIsLocationEnabled(String(isEnabled));
    };

    const checkMockLocation = async () => {
        const status = await checkPermission();
        if (status === RESULTS.GRANTED) {
            const isEnabled = await SafetyUtils.isMockedLocation();
            setIsMockedLocation(String(isEnabled));
        } else {
            console.log("Location Permission Required");
        };
    };

    return (
        <ScrollView style={styles.container}>
            <View style={styles.rowContainer}>
                <Text>Developer Options Enabled ? : {isDevOptionsEnabled}</Text>
                <Button title="Check" onPress={checkDevOptions} />
            </View>
            <View style={styles.rowContainer}>
                <Text>Is Screen Mirroring ? : {isScreenMirroring}</Text>
                <Button title="Check" onPress={checkScreenMirroring} />
            </View>
            {/* ACCESS_NETWORK_STATE permission need to add in Android Manifest */}
            <View style={styles.rowContainer}>
                <Text>Is VPN Active ? : {isVpnActive}</Text>
                <Button title="Check" onPress={checkVpn} />
            </View>
            <View style={styles.rowContainer}>
                <Text>Is Proxy Enabled ? : {isProxyEnabled}</Text>
                <Button title="Check" onPress={checkProxy} />
            </View>
            <View style={styles.rowContainer}>
                <Text>Location Service Enabled ? : {isLocationEnabled}</Text>
                <Button title="Check" onPress={checkLocationEnabled} />
            </View>
            {/* Location Permission Required */}
            <View style={styles.rowContainer}>
                <Text>Is Mocked Location ? : {isMockedLocation}</Text>
                <Button title="Check" onPress={checkMockLocation} />
            </View>
        </ScrollView>
    );

};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        marginTop: 40,
        paddingBottom: 40,
        paddingHorizontal: "3%"
    },
    rowContainer: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        paddingVertical: 20,
        borderRadius: 15,
        borderWidth: 1,
        borderColor: "black",
        paddingHorizontal: 10,
        marginVertical: 10
    }
});