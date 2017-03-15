package xray;

import com.jfrog.xray.client.Xray;
import com.jfrog.xray.client.impl.XrayClient;
import configuration.JfrogGlobalSettings;
import configuration.XrayServerConfig;

/**
 * Created by romang on 2/27/17.
 */
public class ClientUtils {

    public static Xray createClient() {
        XrayServerConfig xrayConfig = JfrogGlobalSettings.getInstance().getXrayConfig();
        return XrayClient.create(xrayConfig.getUrl(), xrayConfig.getUsername(), xrayConfig.getPassword());
    }
}
