/**
 * Copyright © 2022 Safeheron All Rights Reserved
 */
package org.web3j.crypto;

import org.junit.jupiter.api.Test;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by chenjh on 2022/7/22.
 * <p>
 */
public class SignTypedDataTest {

    private String getResource(String jsonFile) throws IOException {
        return new String(
                Files.readAllBytes(Paths.get(jsonFile).toAbsolutePath()), StandardCharsets.UTF_8);
    }

    @Test
    public void testAllSign() throws IOException {
        testEthSign();
        testPersonalSign();
        testSignTypedDataV1();
        testSignTypedDataV3();
        testSignTypedDataV4();
        System.out.println("==== sign end ===");
    }

    @Test
    public void testEthSign() {
        String hex = "0x879a053d4800c6354e76c7985a865d2922c82fb5b3f4577b2fe08b998954f2e0";
        byte[] message = Numeric.hexStringToByteArray(hex);
        Sign.SignatureData signatureData =
                Sign.signMessage(message, SampleKeys.KEY_PAIR, false);
        byte[] retval = new byte[65];
        System.arraycopy(signatureData.getR(), 0, retval, 0, 32);
        System.arraycopy(signatureData.getS(), 0, retval, 32, 32);
        System.arraycopy(signatureData.getV(), 0, retval, 64, 1);

        String sig = Numeric.toHexString(retval);
        System.out.println("eth_sign result: " + sig);

        //recover
        ECRecoverTest ecRecover = new ECRecoverTest();
        String address = ecRecover.recoverAddress(sig, message, 3,false);
        System.out.println("eth_sign ecRecover address: " + address);
    }

    @Test
    public void testPersonalSign() {
        String message = "Example `personal_sign` message";
        byte[] messageBytes = message.getBytes();
        Sign.SignatureData signatureData =
                Sign.signPrefixedMessage(messageBytes, SampleKeys.KEY_PAIR);
        byte[] retval = new byte[65];
        System.arraycopy(signatureData.getR(), 0, retval, 0, 32);
        System.arraycopy(signatureData.getS(), 0, retval, 32, 32);
        System.arraycopy(signatureData.getV(), 0, retval, 64, 1);

        String sig = Numeric.toHexString(retval);
        System.out.println("personal_sign result: " + sig);

        //recover
        ECRecoverTest ecRecover = new ECRecoverTest();
        String finalMessage = ECRecoverTest.PERSONAL_MESSAGE_PREFIX + message.length() + message;
        String address = ecRecover.recoverAddress(sig, finalMessage.getBytes(), 3, true);
        System.out.println("personal_sign ecRecover address: " + address);
    }

    @Test
    public void testSignTypedDataV1() throws IOException {
        String input = getResource("build/resources/test/structured_data_json_files/SignTypedData_v1.json");
        List<StructuredDataV1.LegacyTypedData> legacyTypedDataList = JsonUtils.getObjectList(input, StructuredDataV1.LegacyTypedData.class);
        byte[] bytesHash = LegacyTypedDataEncoder.legacyTypedSignatureHash(legacyTypedDataList);
        Sign.SignatureData signatureData = Sign.signMessage(bytesHash, SampleKeys.KEY_PAIR, false);
        byte[] retval = new byte[65];
        System.arraycopy(signatureData.getR(), 0, retval, 0, 32);
        System.arraycopy(signatureData.getS(), 0, retval, 32, 32);
        System.arraycopy(signatureData.getV(), 0, retval, 64, 1);

        String sig = Numeric.toHexString(retval);
        System.out.println("signTypedData result: " + sig);

        //recover
        ECRecoverTest ecRecover = new ECRecoverTest();
        String address = ecRecover.recoverAddress(sig, bytesHash, 3, false);
        System.out.println("signTypedData ecRecover address: " + address);
    }

    @Test
    public void testSignTypedDataV3() throws IOException {
        StructuredDataEncoder dataEncoder = new StructuredDataEncoder(
                getResource("build/resources/test/structured_data_json_files/SignTypedData_v3.json"));
        byte[] bytes = dataEncoder.hashStructuredData();
        Sign.SignatureData signatureData = Sign.signMessage(bytes, SampleKeys.KEY_PAIR, false);
        byte[] retval = new byte[65];
        System.arraycopy(signatureData.getR(), 0, retval, 0, 32);
        System.arraycopy(signatureData.getS(), 0, retval, 32, 32);
        System.arraycopy(signatureData.getV(), 0, retval, 64, 1);

        String sig = Numeric.toHexString(retval);
        System.out.println("signTypedData_v3 result: " + sig);

        //recover
        byte[] messageData = dataEncoder.getStructuredData();
        ECRecoverTest ecRecover = new ECRecoverTest();
        String address = ecRecover.recoverAddress(sig, messageData, 3, true);
        System.out.println("signTypedData_v3 ecRecover address: " + address);
    }


    @Test
    public void testSignTypedDataV4() throws IOException {
        StructuredDataEncoder dataEncoder = new StructuredDataEncoder(
                getResource("build/resources/test/structured_data_json_files/SignTypedData_v4.json"));
        byte[] bytes = dataEncoder.hashStructuredData();
        Sign.SignatureData signatureData = Sign.signMessage(bytes, SampleKeys.KEY_PAIR, false);
        byte[] retval = new byte[65];
        System.arraycopy(signatureData.getR(), 0, retval, 0, 32);
        System.arraycopy(signatureData.getS(), 0, retval, 32, 32);
        System.arraycopy(signatureData.getV(), 0, retval, 64, 1);

        String sig = Numeric.toHexString(retval);
        System.out.println("signTypedData_v4 result: " + sig);

        //recover
        byte[] messageData = dataEncoder.getStructuredData();
        ECRecoverTest ecRecover = new ECRecoverTest();
        String address = ecRecover.recoverAddress(sig, messageData, 3, true);
        System.out.println("signTypedData_v4 ecRecover address: " + address);
    }


    @Test
    public void testSignTypedDataV4_test() throws IOException {
        StructuredDataEncoder dataEncoder = new StructuredDataEncoder(
                getResource("build/resources/test/structured_data_json_files/SignTypedData_v4_test.json"));
        byte[] bytes = dataEncoder.hashStructuredData();
        Sign.SignatureData signatureData = Sign.signMessage(bytes, SampleKeys.KEY_PAIR, false);
        byte[] retval = new byte[65];
        System.arraycopy(signatureData.getR(), 0, retval, 0, 32);
        System.arraycopy(signatureData.getS(), 0, retval, 32, 32);
        System.arraycopy(signatureData.getV(), 0, retval, 64, 1);

        String sig = Numeric.toHexString(retval);
        System.out.println("signTypedData_v4 result: " + sig);

        //recover
        byte[] messageData = dataEncoder.getStructuredData();
        ECRecoverTest ecRecover = new ECRecoverTest();
        String address = ecRecover.recoverAddress(sig, messageData, 3, true);
        System.out.println("signTypedData_v4 ecRecover address: " + address);
    }
}
