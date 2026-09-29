package moe.shizuku.manager;

import moe.shizuku.manager.utils.MultiLocaleEntity;

public class Helps {

    public static final MultiLocaleEntity ADB = new MultiLocaleEntity();
    public static final MultiLocaleEntity ADB_ANDROID11 = new MultiLocaleEntity();
    public static final MultiLocaleEntity APPS = new MultiLocaleEntity();
    public static final MultiLocaleEntity HOME = new MultiLocaleEntity();
    public static final MultiLocaleEntity DOWNLOAD = new MultiLocaleEntity();
    public static final MultiLocaleEntity SUI = new MultiLocaleEntity();
    public static final MultiLocaleEntity RISH = new MultiLocaleEntity();
    public static final MultiLocaleEntity ADB_PERMISSION = new MultiLocaleEntity();

    private static final String PROJECT = "https://github.com/lmh-codes/shizuku";

    static {
        ADB.put("zh-CN", PROJECT);
        ADB.put("zh-TW", PROJECT);
        ADB.put("en", PROJECT);

        ADB_ANDROID11.put("zh-CN", PROJECT);
        ADB_ANDROID11.put("zh-TW", PROJECT);
        ADB_ANDROID11.put("en", PROJECT);

        APPS.put("zh-CN", PROJECT);
        APPS.put("zh-TW", PROJECT);
        APPS.put("en", PROJECT);

        HOME.put("en", PROJECT);

        DOWNLOAD.put("zh-CN", PROJECT + "/releases");
        DOWNLOAD.put("zh-TW", PROJECT + "/releases");
        DOWNLOAD.put("en", PROJECT + "/releases");

        ADB_PERMISSION.put("zh-CN", PROJECT);
        ADB_PERMISSION.put("zh-TW", PROJECT);
        ADB_PERMISSION.put("en", PROJECT);

        SUI.put("en", PROJECT);

        RISH.put("en", PROJECT);
    }
}
