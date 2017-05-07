package xray.persistency;

import com.jfrog.xray.client.services.summary.License;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romang on 4/12/17.
 */
public class XrayLicense implements Comparable<XrayLicense> {
    public List<String> components = new ArrayList<>();
    public String fullName;
    public String name;

    public XrayLicense() {
    }

    public XrayLicense(License license) {
        components = license.getComponents();
        fullName = license.getFullName();
        name = license.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XrayLicense) {
            return name.equals(((XrayLicense) obj).name);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int result = fullName != null ? fullName.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NotNull XrayLicense o) {
        return 0;
    }
}
