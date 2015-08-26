package br.com.compremelhor.useful;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by adriano on 25/08/15.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static void main(String args[]) throws IOException, SQLException {
        writeConfigFile("ormlite_config.txt");
    }
}
