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


public class Main {

    static String fromAddress = "RMC0108aE381335Bba1F5a3293D501947D6174de367";
    static  String privateKeyStr = "6ab0638768979e4a551a2c81b90c943cb12e07819bee721be74aaf481919bb2b";
    static String toAddress = "RMC853408399e06E37a8F8f05AE71057c5Ba5bFA85E";
    static String outBalance = "500";//eth
    static int chainid = 2021;

    public static void main(String[] args) throws IOException {


        Web3j web3j = Web3j.build(new HttpService("http://chain-node.rmc.city/"));

        BigInteger b=web3j.ethGetBalance(fromAddress,DefaultBlockParameterName.LATEST).send().getBalance();
        BigDecimal bigDecimal = Convert.fromWei(new BigDecimal(b),Convert.Unit.ETHER);
        System.out.println("---->"+bigDecimal);

        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(fromAddress,
                        DefaultBlockParameterName.LATEST).send();

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
