package rmc.web3j;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;


//
//        address:RMCef18bc670b9edbbb5f801fd8e1e1f628ee6a7c1f  privateKey:6af7ba0e55d45a22d9b2d71687ef733b687c95a52b1fdf30ac72e745ef318db9
//        address:RMC57b83b584571565788992b05e073682aa66f02ae  privateKey:0096a1e5819521dde001aabcb06fc9570c48a1426285114af253c9f1b24095aba0
//        address:RMCab94030f656b81226cd4bce20aa546bd5e785fd9  privateKey:6c34ac7fa68fdb1f773549dcb11174465be83c3e33f603ef13399784710755f0
//        address:RMC512956e4367d43c7bf86f56b7c7f4d2ec01d7fd2  privateKey:51cb7059e46fc18eee4f66ee4deca6002b88c942116a0ae56f61109ad476d03c
//        address:RMCd3ebbfc64b098245a5527e31fabebec29a9e62ca  privateKey:00a1aaab9c9973a87341a9f7e39b3200d3ba410839a214d33f1f5e5f9c0a5884d4
//        address:RMC64fb32b3937a8162fe5a5add1ed9d1bc2891f2fb  privateKey:5733e0a9ce5953e75becc49a9e59b251abbc6ebf6d0647df6ac3bd95eab48b36
//        address:RMCceb922ccc12f05c40151ac6d2166d174b841d026  privateKey:00a97491edc08d916671cf7b384d50c80f2ccfa6c0f6c87657c8753ff6161d63c0
//        address:RMC7cbd053277f6234ea093b9c07617e3defe090a2a  privateKey:00faea55fc6efe4cc8b16aa0cbe429627b0190f03d2a00e1f11296961c6bf6ed5f
//        address:RMCa1c0b059da0715e9e8b0d43849d096a71f7541ca  privateKey:69be16d38903e72b89a1af4c2a6570cd0403d7777a429d094f4d393d406ff687
//        address:RMC5b99199369c19b90a79086df016f504cdf334de4  privateKey:284d5ba5cc1c73dbcd8dc7c5d8c40232acac26b242a58dbd5667282fa9342d37

public class Main {
//
//    static String fromAddress = "RMC0108aE381335Bba1F5a3293D501947D6174de367";
//    static  String privateKeyStr = "6ab0638768979e4a551a2c81b90c943cb12e07819bee721be74aaf481919bb2b";

    static String fromAddress = "RMC5b99199369c19b90a79086df016f504cdf334de4";
    static String privateKeyStr = "284d5ba5cc1c73dbcd8dc7c5d8c40232acac26b242a58dbd5667282fa9342d37";


    static String toAddress = "RMCa1c0b059da0715e9e8b0d43849d096a71f7541ca";
    static String outBalance = "2564";//eth
    static int chainid = 2021;

    public static void main(String[] args) throws IOException {


        Web3j web3j = Web3j.build(new HttpService("http://chain-node.rmc.city/"));

        BigInteger b=web3j.ethGetBalance(fromAddress,DefaultBlockParameterName.LATEST).send().getBalance();
        BigDecimal bigDecimal = Convert.fromWei(new BigDecimal(b),Convert.Unit.ETHER);
        System.out.println("---->"+bigDecimal);

        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(fromAddress,
                        DefaultBlockParameterName.PENDING).send();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        byte[] privateKey =
                Hex.decode(privateKeyStr.replace("RMC", ""));

        Credentials keys = Credentials.create(ECKeyPair.create(privateKey));

        BigInteger weiBalance =Convert.toWei(outBalance, Convert.Unit.ETHER).toBigInteger();

        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = new BigInteger("21000");

        RmRawTransaction etherTransaction = RmRawTransaction.createTransaction(nonce,
                gasPrice,
                gasLimit,
                toAddress, weiBalance, "");


        byte[] bytes = RmTransactionEncoder.signMessage(etherTransaction, chainid, keys, true);
        String hexValue = Numeric.toHexString(bytes);

        EthSendTransaction ethSendTransaction =
                web3j.ethSendRawTransaction(hexValue).send();

        boolean hasError = ethSendTransaction.hasError();
        if (hasError) {
            System.out.println(hasError);
            return;
        }

        String transactionHash = ethSendTransaction.getTransactionHash();
        System.out.println("txHash---> "+transactionHash);
    }

}
