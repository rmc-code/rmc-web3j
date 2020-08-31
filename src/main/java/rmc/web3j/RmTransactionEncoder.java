package rmc.web3j;

import org.web3j.crypto.Credentials;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Name: RmTransactionEncoder
 * Author: Administrator
 * Email:
 * Comment: //新的RM交易编码规则
 * Date: 2020-05-19 14:11
 */
public class RmTransactionEncoder {
    private static final int CHAIN_ID_INC = 35;
    private static final int LOWER_REAL_V = 27;

    public static byte[] signMessage(RmRawTransaction rawTransaction, Credentials credentials) {
        byte[] encodedTransaction = encode(rawTransaction);
        RmSign.SignatureData signatureData = RmSign.signMessage(encodedTransaction,
                                                                credentials.getEcKeyPair());

        return encode(rawTransaction, signatureData, false);
    }

    public static byte[] signMessage(RmRawTransaction rawTransaction, long chainId,
                                     Credentials credentials, boolean includeType) {
        byte[] encodedTransaction = encode(rawTransaction, chainId, includeType);
        RmSign.SignatureData signatureData = RmSign.signMessage(encodedTransaction,
                                                                credentials.getEcKeyPair());

        RmSign.SignatureData eip155SignatureData = createEip155SignatureData(signatureData,
                                                                             chainId);
        return encode(rawTransaction, eip155SignatureData, includeType);
    }

    @Deprecated
    public static byte[] signMessage(RmRawTransaction rawTransaction, byte chainId,
                                     Credentials credentials) {
        return signMessage(rawTransaction, (long) chainId, credentials, false);
    }

    public static RmSign.SignatureData createEip155SignatureData(RmSign.SignatureData signatureData, long chainId) {
        BigInteger v = Numeric.toBigInt(signatureData.getV());
        v = v.subtract(BigInteger.valueOf(LOWER_REAL_V));
        v = v.add(BigInteger.valueOf(chainId * 2));
        v = v.add(BigInteger.valueOf(CHAIN_ID_INC));

        return new RmSign.SignatureData(v.toByteArray(), signatureData.getR(),
                                        signatureData.getS());
    }

    @Deprecated
    public static RmSign.SignatureData createEip155SignatureData(RmSign.SignatureData signatureData, byte chainId) {
        return createEip155SignatureData(signatureData, (long) chainId);
    }

    public static byte[] encode(RmRawTransaction rawTransaction) {
        return encode(rawTransaction, null, false);
    }

    public static byte[] encode(RmRawTransaction rawTransaction, long chainId,
                                boolean includeType) {
        RmSign.SignatureData signatureData = new RmSign.SignatureData(longToBytes(chainId),
                                                                      new byte[] {}, new byte[] {});
        return encode(rawTransaction, signatureData, includeType);
    }

    @Deprecated
    public static byte[] encode(RmRawTransaction rawTransaction, byte chainId) {
        return encode(rawTransaction, (long) chainId, false);
    }

    private static byte[] encode(RmRawTransaction rawTransaction,
                                 RmSign.SignatureData signatureData, boolean includeType) {
        List<RlpType> values = asRlpValues(rawTransaction, signatureData, includeType);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    private static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static List<RlpType> asRlpValues(RmRawTransaction rawTransaction,
                                            RmSign.SignatureData signatureData,
                                            boolean includeType) {
        List<RlpType> result = new ArrayList<>();

        result.add(RlpString.create(rawTransaction.getNonce()));
        result.add(RlpString.create(rawTransaction.getGasPrice()));
        result.add(RlpString.create(rawTransaction.getGasLimit()));

        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = rawTransaction.getTo();
        if (to != null && to.length() > 2) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            if (to.length() > 40) {
                to = to.substring(to.length() - 40);
            }
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }

        result.add(RlpString.create(rawTransaction.getValue()));

        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
        result.add(RlpString.create(data));
        if (includeType) {
            result.add(RlpString.create(BigInteger.valueOf(0)));
        }

        // add gas premium and fee cap if this is an EIP-1559 transaction
        if (rawTransaction.isEIP1559Transaction()) {
            result.add(RlpString.create(rawTransaction.getGasPremium()));
            result.add(RlpString.create(rawTransaction.getFeeCap()));
        }

        if (signatureData != null) {
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getV())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
        }

        return result;
    }
}
