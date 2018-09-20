import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class Test {
    public static void main(String[] args) throws DecoderException {
        String part1 = "e2994a563396cf3194b8013e0f8958bc";
        String part2 = "fa6bbd3fd7683db8e1e9733025b7ef73";
        String part3 =  "6d2c3ee7cb445c18431d0a9a651ec6ef";

        String part11 = "9961bbe5d7c70a5f0e23d63bc7433b01";
        String part22 = "bfbd30c675df62d67f02d0efa72bc1ac";
        String part33 = "92589dcb2dd1bcc29ab1ee6b8eb7f6aa";

        String md5 = DigestUtils.md5Hex(Hex.decodeHex(part11+part22+part33));
        System.out.println(md5);
    }
}
