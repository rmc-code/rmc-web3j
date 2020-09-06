package rmc.web3j;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Balance {

    static String fromAddress = "RMCa1c0b059da0715e9e8b0d43849d096a71f7541ca";

    public static void main(String[] args) throws IOException {
        Web3j web3j = Web3j.build(new HttpService("http://chain-node.rmc.city/"));

        BigInteger b=web3j.ethGetBalance(fromAddress, DefaultBlockParameterName.LATEST).send().getBalance();
        BigDecimal bigDecimal = Convert.fromWei(new BigDecimal(b),Convert.Unit.ETHER);
        System.out.println("---->"+bigDecimal);
    }
}
