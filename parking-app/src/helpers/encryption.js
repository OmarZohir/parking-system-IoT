import * as CryptoJS from 'crypto-js';

export const decrypt = (encrypted) => {
    return CryptoJS.AES.decrypt(encrypted, "otsI2tRrmhGlDMk").toString(CryptoJS.enc.Utf8);
};

export const encrypt = (string) => {
    return CryptoJS.AES.encrypt(string, "otsI2tRrmhGlDMk").toString();
};
