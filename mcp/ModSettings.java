package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import de.matthiasmann.twl.Widget;


/**
 * Main interface class for Settings API
 * 
 * @author lahwran
 */
public class ModSettings
{
    /**
     * A list of all ModSettings instances.
     */
    public static ArrayList<ModSettings> all = new ArrayList<ModSettings>();
    /**
     * A map of context names and the directories they save to.
     */
    public static HashMap<String, String> contextDatadirs;
    /**
     * The current context.
     */
    public static String currentContext;
    /**
     * Debug mode flag. Should always be false.
     */
    public static final boolean debug = false;
    private static Minecraft minecraftInstance;
    static
    {
        ModSettings.contextDatadirs = new HashMap<String, String>();
        ModSettings.currentContext = "";
        ModSettings.contextDatadirs.put("", "mods");
    }
    
    /**
     * Debug printer. This prints to System.out, and only works when in debug
     * mode.
     * 
     * @param s
     *            The string to output.
     */
    public static void dbgout(String s)
    {
        if (ModSettings.debug)
        {
            System.out.println(s);
        }
    }
    
    /**
     * Returns, and creates if needed, an application directory.
     * 
     * @param app
     *            The name of the application.
     * @return A File reference to the directory created.
     */
    public static File getAppDir(String app)
    {
        return Minecraft.getAppDir(app);
    }
    
    /**
     * This finds and returns a Minecraft instance. It caches it if it has
     * already been located.
     * 
     * @return The minecraft instance.
     */
    public static Minecraft getMcinst()
    {
        if (ModSettings.minecraftInstance != null)
        {
            return ModSettings.minecraftInstance;
        }
        Field f;
        try
        {
            f = Minecraft.class.getDeclaredFields()[1];
            f.setAccessible(true);
            Minecraft m = (Minecraft) f.get(null);
            if (m != null)
            {
                ModSettings.minecraftInstance = m;
                return m;
            }
            f = Thread.class.getDeclaredField("target");
            f.setAccessible(true);
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            int count = group.activeCount();
            Thread[] threads = new Thread[count];
            group.enumerate(threads);
            for (int i = 0; i < threads.length; i++)
            {
                if (threads[i].getName().equals("Minecraft main thread"))
                {
                    m = (Minecraft) f.get(threads[i]);
                    if (m != null)
                    {
                        ModSettings.minecraftInstance = m;
                        return m;
                    }
                }
            }
        }
        catch (Throwable t)
        {
            throw new RuntimeException(t);
        }
        throw new RuntimeException(
                "You are a godless monkey! why are you doing weird things with the innards of minecraft?");
    }
    
    /**
     * Loads all saved settings for a specific context.
     * 
     * @param context
     *            The context to load from.
     */
    public static void loadAll(String context)
    {
        for (int i = 0; i < ModSettings.all.size(); i++)
        {
            ModSettings.all.get(i).load(context);
        }
    }
    
    /**
     * Set the Minecraft instance that getMcinst returns.
     * 
     * @param m
     *            The Minecraft Instance.
     */
    public static void presetMcint(Minecraft m)
    {
        ModSettings.minecraftInstance = m;
    }
    
    /**
     * Sets the context for mods. This means you can specify a context on a per
     * world / per server basis, or anything else you would prefer. This will
     * carry thoughout all mods.
     * 
     * @param name
     *            The name reference of the context.
     * @param location
     *            The location that this context stores and loads data from.
     */
    public static void setContext(String name, String location)
    {
        if (name != null)
        {
            ModSettings.contextDatadirs.put(name, location);
            ModSettings.currentContext = name;
            if (!name.equals(""))
            {
                ModSettings.loadAll(ModSettings.currentContext);
            }
        }
        else
        {
            ModSettings.currentContext = "";
        }
    }
    
    /**
     * Mod name as used in .minecraft/mods/${modbackendname}/
     */
    public String backendname;
    /**
     * all registered settings for this mod
     */
    @SuppressWarnings("rawtypes")
    public ArrayList<Setting> Settings;
    /**
     * Whether or not Settings have been loaded for this mod.
     */
    public boolean settingsLoaded = false;
    
    /**
     * @param modbackendname
     *            used to initialize class modbackendname field
     */
    @SuppressWarnings("rawtypes")
    public ModSettings(String modbackendname)
    {
        backendname = modbackendname;
        Settings = new ArrayList<Setting>();
        ModSettings.all.add(this);
    }
    
    /**
     * convenience boolean setting adder
     */
    public SettingBoolean addSetting(ModSettingScreen screen, String nicename,
            String backendname, boolean value)
    {
        SettingBoolean s = new SettingBoolean(backendname, value);
        WidgetBoolean w = new WidgetBoolean(s, nicename);
        screen.append(w);
        append(s);
        return s;
    }
    
    /**
     * convenience boolean setting adder
     */
    public SettingBoolean addSetting(ModSettingScreen screen, String nicename,
            String backendname, boolean value, String truestring,
            String falsestring)
    {
        SettingBoolean s = new SettingBoolean(backendname, value);
        WidgetBoolean w = new WidgetBoolean(s, nicename, truestring,
                falsestring);
        screen.append(w);
        append(s);
        return s;
    }
    
    /**
     * convenience float setting adder
     */
    public SettingFloat addSetting(ModSettingScreen screen, String nicename,
            String backendname, float value)
    {
        SettingFloat s = new SettingFloat(backendname, value);
        WidgetFloat w = new WidgetFloat(s, nicename);
        screen.append(w);
        append(s);
        return s;
    }
    
    /**
     * convenience float setting adder
     */
    public SettingFloat addSetting(ModSettingScreen screen, String nicename,
            String backendname, float value, float min, float step, float max)
    {
        SettingFloat s = new SettingFloat(backendname, value, min, step, max);
        WidgetFloat w = new WidgetFloat(s, nicename);
        screen.append(w);
        append(s);
        return s;
    }
    
    /**
     * convenience key setting adder
     */
    public SettingKey addSetting(ModSettingScreen screen, String nicename,
            String backendname, int value)
    {
        SettingKey s = new SettingKey(backendname, value);
        WidgetKeybinding w = new WidgetKeybinding(s, nicename);
        screen.append(w);
        append(s);
        return s;
    }
    
    /**
     * convenience int setting adder
     */
    public SettingInt addSetting(ModSettingScreen screen, String nicename,
            String backendname, int value, int min, int max)
    {
        SettingInt s = new SettingInt(backendname, value, min, 1, max);
        WidgetInt w = new WidgetInt(s, nicename);
        screen.append(w);
        append(s);
        return s;
    }
    
    /**
     * convenience int setting adder
     */
    public SettingInt addSetting(ModSettingScreen screen, String nicename,
            String backendname, int value, int min, int step, int max)
    {
        SettingInt s = new SettingInt(backendname, value, min, step, max);
        WidgetInt w = new WidgetInt(s, nicename);
        screen.append(w);
        append(s);
        return s;
    }
    
    /**
     * convenience multi setting adder
     */
    public SettingMulti addSetting(ModSettingScreen screen, String nicename,
            String backendname, int value, String... labels)
    {
        SettingMulti s = new SettingMulti(backendname, value, labels);
        WidgetMulti w = new WidgetMulti(s, nicename);
        screen.append(w);
        append(s);
        return s;
    }
    
    /**
     * convenience text setting adder
     */
    public SettingText addSetting(ModSettingScreen screen, String nicename,
            String backendname, String value)
    {
        SettingText s = new SettingText(backendname, value);
        WidgetText w = new WidgetText(s, nicename);
        screen.append(w);
        append(s);
        return s;
    }
    
    /**
     * convenience boolean setting adder
     */
    public SettingBoolean addSetting(Widget w2, String nicename,
            String backendname, boolean value)
    {
        SettingBoolean s = new SettingBoolean(backendname, value);
        WidgetBoolean w = new WidgetBoolean(s, nicename);
        w2.add(w);
        append(s);
        return s;
    }
    
    /**
     * convenience boolean setting adder
     */
    public SettingBoolean addSetting(Widget w2, String nicename,
            String backendname, boolean value, String truestring,
            String falsestring)
    {
        SettingBoolean s = new SettingBoolean(backendname, value);
        WidgetBoolean w = new WidgetBoolean(s, nicename, truestring,
                falsestring);
        w2.add(w);
        append(s);
        return s;
    }
    
    /**
     * convenience float setting adder
     */
    public SettingFloat addSetting(Widget w2, String nicename,
            String backendname, float value)
    {
        SettingFloat s = new SettingFloat(backendname, value);
        WidgetFloat w = new WidgetFloat(s, nicename);
        w2.add(w);
        append(s);
        return s;
    }
    
    /**
     * convenience float setting adder
     */
    public SettingFloat addSetting(Widget w2, String nicename,
            String backendname, float value, float min, float step, float max)
    {
        SettingFloat s = new SettingFloat(backendname, value, min, step, max);
        WidgetFloat w = new WidgetFloat(s, nicename);
        w2.add(w);
        append(s);
        return s;
    }
    
    /**
     * convenience key setting adder
     */
    public SettingKey addSetting(Widget w2, String nicename,
            String backendname, int value)
    {
        SettingKey s = new SettingKey(backendname, value);
        WidgetKeybinding w = new WidgetKeybinding(s, nicename);
        w2.add(w);
        append(s);
        return s;
    }
    
    /**
     * convenience int setting adder
     */
    public SettingInt addSetting(Widget w2, String nicename,
            String backendname, int value, int min, int max)
    {
        SettingInt s = new SettingInt(backendname, value, min, 1, max);
        WidgetInt w = new WidgetInt(s, nicename);
        w2.add(w);
        append(s);
        return s;
    }
    
    /**
     * convenience int setting adder
     */
    public SettingInt addSetting(Widget w2, String nicename,
            String backendname, int value, int min, int step, int max)
    {
        SettingInt s = new SettingInt(backendname, value, min, step, max);
        WidgetInt w = new WidgetInt(s, nicename);
        w2.add(w);
        append(s);
        return s;
    }
    
    /**
     * convenience multi setting adder
     */
    public SettingMulti addSetting(Widget w2, String nicename,
            String backendname, int value, String... labels)
    {
        SettingMulti s = new SettingMulti(backendname, value, labels);
        WidgetMulti w = new WidgetMulti(s, nicename);
        w2.add(w);
        append(s);
        return s;
    }
    
    /**
     * convenience text setting adder
     */
    public SettingText addSetting(Widget w2, String nicename,
            String backendname, String value)
    {
        SettingText s = new SettingText(backendname, value);
        WidgetText w = new WidgetText(s, nicename);
        w2.add(w);
        append(s);
        return s;
    }
    
    /**
     * add a setting to be saved.
     * 
     * @param s
     *            setting to add - sets s.parent as well, don't add a setting to
     *            more than one modsettings
     */
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    public void append(Setting s)
    {
        Settings.add(s);
        s.parent = this;
    }
    
    /**
     * Copies the saved settings from one context to another.
     * 
     * @param src
     *            The source context from which to copy.
     * @param dest
     *            The destination context to save to.
     */
    public void copyContextAll(String src, String dest)
    {
        for (int i = 0; i < Settings.size(); i++)
        {
            Settings.get(i).copyContext(src, dest);
        }
    }
    
    /**
     * Get a list of all Boolean settings for the current context.
     * 
     * @return The list of settings.
     */
    public ArrayList<SettingBoolean> getAllBooleanSettings()
    {
        return getAllBooleanSettings(ModSettings.currentContext);
    }
    
    /**
     * Get a list of all Boolean settings for the specified context.
     * 
     * @param context
     *            The context from which to copy from.
     * @return The list of settings.
     */
    @SuppressWarnings("rawtypes")
    public ArrayList<SettingBoolean> getAllBooleanSettings(String context)
    {
        ArrayList<SettingBoolean> settings = new ArrayList<SettingBoolean>();
        for (Setting setting : Settings)
        {
            if (!SettingBoolean.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            settings.add((SettingBoolean) setting);
        }
        return settings;
    }
    
    /**
     * Get a list of all Float settings for the current context.
     * 
     * @return The list of settings.
     */
    public ArrayList<SettingFloat> getAllFloatSettings()
    {
        return getAllFloatSettings(ModSettings.currentContext);
    }
    
    /**
     * Get a list of all Float settings for the specified context.
     * 
     * @param context
     *            The context from which to copy from.
     * @return The list of settings.
     */
    @SuppressWarnings("rawtypes")
    public ArrayList<SettingFloat> getAllFloatSettings(String context)
    {
        ArrayList<SettingFloat> settings = new ArrayList<SettingFloat>();
        for (Setting setting : Settings)
        {
            if (!SettingFloat.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            settings.add((SettingFloat) setting);
        }
        return settings;
    }
    
    /**
     * Get a list of all Int settings for the current context.
     * 
     * @return The list of settings.
     */
    public ArrayList<SettingInt> getAllIntSettings()
    {
        return getAllIntSettings(ModSettings.currentContext);
    }
    
    /**
     * Get a list of all Int settings for the specified context.
     * 
     * @param context
     *            The context from which to copy from.
     * @return The list of settings.
     */
    @SuppressWarnings("rawtypes")
    public ArrayList<SettingInt> getAllIntSettings(String context)
    {
        ArrayList<SettingInt> settings = new ArrayList<SettingInt>();
        for (Setting setting : Settings)
        {
            if (!SettingInt.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            settings.add((SettingInt) setting);
        }
        return settings;
    }
    
    /**
     * Get a list of all Key settings for the current context.
     * 
     * @return The list of settings.
     */
    public ArrayList<SettingKey> getAllKeySettings()
    {
        return getAllKeySettings(ModSettings.currentContext);
    }
    
    /**
     * Get a list of all Key settings for the specified context.
     * 
     * @param context
     *            The context from which to copy from.
     * @return The list of settings.
     */
    @SuppressWarnings("rawtypes")
    public ArrayList<SettingKey> getAllKeySettings(String context)
    {
        ArrayList<SettingKey> settings = new ArrayList<SettingKey>();
        for (Setting setting : Settings)
        {
            if (!SettingKey.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            settings.add((SettingKey) setting);
        }
        return settings;
    }
    
    /**
     * Get a list of all Multi settings for the current context.
     * 
     * @return The list of settings.
     */
    public ArrayList<SettingMulti> getAllMultiSettings()
    {
        return getAllMultiSettings(ModSettings.currentContext);
    }
    
    /**
     * Get a list of all Multi settings for the specified context.
     * 
     * @param context
     *            The context from which to copy from.
     * @return The list of settings.
     */
    @SuppressWarnings("rawtypes")
    public ArrayList<SettingMulti> getAllMultiSettings(String context)
    {
        ArrayList<SettingMulti> settings = new ArrayList<SettingMulti>();
        for (Setting setting : Settings)
        {
            if (!SettingMulti.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            settings.add((SettingMulti) setting);
        }
        return settings;
    }
    
    /**
     * Get a list of all Text settings for the current context.
     * 
     * @return The list of settings.
     */
    public ArrayList<SettingText> getAllTextSettings()
    {
        return getAllTextSettings(ModSettings.currentContext);
    }
    
    /**
     * Get a list of all Text settings for the specified context.
     * 
     * @param context
     *            The context from which to copy from.
     * @return The list of settings.
     */
    @SuppressWarnings("rawtypes")
    public ArrayList<SettingText> getAllTextSettings(String context)
    {
        ArrayList<SettingText> settings = new ArrayList<SettingText>();
        for (Setting setting : Settings)
        {
            if (!SettingText.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            settings.add((SettingText) setting);
        }
        return settings;
    }
    
    /**
     * Gets the value of a setting by backend name from the current context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @return The boolean value.
     */
    public Boolean getBooleanSetting(String backendName)
    {
        return getBooleanSetting(backendName, ModSettings.currentContext);
    }
    
    /**
     * Gets the value of a setting by backend name from the specified context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @param context
     *            The context from which to copy from.
     * @return The boolean value.
     */
    @SuppressWarnings("rawtypes")
    public Boolean getBooleanSetting(String backendName, String context)
    {
        for (Setting setting : Settings)
        {
            if (!SettingBoolean.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            if (setting.backendName.equals(backendName))
            {
                return (((SettingBoolean) setting).get(context));
            }
        }
        throw new InvalidParameterException("SettingBoolean '" + backendName
                + "' not found.");
    }
    
    /**
     * Gets the value of a setting by backend name from the current context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @return The Float value.
     */
    public Float getFloatSetting(String backendName)
    {
        return getFloatSetting(backendName, ModSettings.currentContext);
    }
    
    /**
     * Gets the value of a setting by backend name from the specified context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @param context
     *            The context from which to copy from.
     * @return The Float value.
     */
    @SuppressWarnings("rawtypes")
    public Float getFloatSetting(String backendName, String context)
    {
        for (Setting setting : Settings)
        {
            if (!SettingFloat.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            if (setting.backendName.equals(backendName))
            {
                return (((SettingFloat) setting).get(context));
            }
        }
        throw new InvalidParameterException("SettingFloat '" + backendName
                + "' not found.");
    }
    
    /**
     * Gets the value of a setting by backend name from the current context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @return The Int value.
     */
    public Integer getIntSetting(String backendName)
    {
        return getIntSetting(backendName, ModSettings.currentContext);
    }
    
    /**
     * Gets the value of a setting by backend name from the specified context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @param context
     *            The context from which to copy from.
     * @return The Int value.
     */
    @SuppressWarnings("rawtypes")
    public Integer getIntSetting(String backendName, String context)
    {
        for (Setting setting : Settings)
        {
            if (!SettingInt.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            if (setting.backendName.equals(backendName))
            {
                return (((SettingInt) setting).get(context));
            }
        }
        throw new InvalidParameterException("SettingInt '" + backendName
                + "' not found.");
    }
    
    /**
     * Gets the value of a setting by backend name from the current context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @return The Key value.
     */
    public Integer getKeySetting(String backendName)
    {
        return getKeySetting(backendName, ModSettings.currentContext);
    }
    
    /**
     * Gets the value of a setting by backend name from the specified context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @param context
     *            The context from which to copy from.
     * @return The Key value.
     */
    @SuppressWarnings("rawtypes")
    public Integer getKeySetting(String backendName, String context)
    {
        for (Setting setting : Settings)
        {
            if (!SettingKey.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            if (setting.backendName.equals(backendName))
            {
                return (((SettingKey) setting).get(context));
            }
        }
        throw new InvalidParameterException("SettingKey '" + backendName
                + "' not found.");
    }
    
    /**
     * Gets the value of a setting by backend name from the current context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @return The Multi value.
     */
    public Integer getMultiSetting(String backendName)
    {
        return getMultiSetting(backendName, ModSettings.currentContext);
    }
    
    /**
     * Gets the value of a setting by backend name from the specified context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @param context
     *            The context from which to copy from.
     * @return The Multi value.
     */
    @SuppressWarnings("rawtypes")
    public Integer getMultiSetting(String backendName, String context)
    {
        for (Setting setting : Settings)
        {
            if (!SettingMulti.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            if (setting.backendName.equals(backendName))
            {
                return (((SettingMulti) setting).get(context));
            }
        }
        throw new InvalidParameterException("SettingMulti '" + backendName
                + "' not found.");
    }
    
    /**
     * Gets the value of a setting by backend name from the current context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @return The text label for the value.
     */
    public String getMultiSettingLabel(String backendName)
    {
        return getMultiSettingLabel(backendName, ModSettings.currentContext);
    }
    
    /**
     * Gets the value of a setting by backend name from the specified context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @param context
     *            The context from which to copy from.
     * @return The text label for the value.
     */
    @SuppressWarnings("rawtypes")
    public String getMultiSettingLabel(String backendName, String context)
    {
        for (Setting setting : Settings)
        {
            if (!SettingMulti.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            if (setting.backendName.equals(backendName))
            {
                return (((SettingMulti) setting).getLabel(context));
            }
        }
        throw new InvalidParameterException("SettingMulti '" + backendName
                + "' not found.");
    }
    
    /**
     * Gets the value of a setting by backend name from the current context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @return The Text value.
     */
    public String getTextSetting(String backendName)
    {
        return getTextSetting(backendName, ModSettings.currentContext);
    }
    
    /**
     * Gets the value of a setting by backend name from the specified context.
     * 
     * @param backendName
     *            The backend name of the setting.
     * @param context
     *            The context from which to copy from.
     * @return The Text value.
     */
    @SuppressWarnings("rawtypes")
    public String getTextSetting(String backendName, String context)
    {
        for (Setting setting : Settings)
        {
            if (!SettingText.class.isAssignableFrom(setting.getClass()))
            {
                continue;
            }
            if (setting.backendName.equals(backendName))
            {
                return (((SettingText) setting).get(context));
            }
        }
        throw new InvalidParameterException("SettingText '" + backendName
                + "' not found.");
    }
    
    /**
     * Loads the settings for the default context.
     */
    public void load()
    {
        load("");
        settingsLoaded = true;
    }
    
    /**
     * must be called after all settings are added for loading/saving to work.
     * loads from .minecraft/mods/$backendname/guiconfig.properties if it
     * exists. coming soon: set name of config file
     * 
     * @param context
     *            The context to load from.
     */
    @SuppressWarnings("rawtypes")
    public void load(String context)
    {
        for (;;)
        {
            try
            {
                if (ModSettings.contextDatadirs.get(context) == null)
                {
                    break;
                }
                File path = ModSettings.getAppDir("minecraft/"
                        + ModSettings.contextDatadirs.get(context) + "/"
                        + backendname + "/");
                if (!path.exists())
                {
                    break;
                }
                File file = new File(path, "guiconfig.properties");
                if (!file.exists())
                {
                    break;
                }
                Properties p = new Properties();
                p.load(new FileInputStream(file));
                for (int i = 0; i < Settings.size(); i++)
                {
                    if (Settings.get(i) instanceof Setting)
                    {
                        ModSettings.dbgout("setting load");
                        Setting z = Settings.get(i);
                        if (p.containsKey(z.backendName))
                        {
                            ModSettings.dbgout("setting "
                                    + (String) p.get(z.backendName));
                            z.fromString((String) p.get(z.backendName), context);
                        }
                    }
                }
                break;
            }
            catch (Exception e)
            {
                System.out.println(e);
                break;
            }
        }
    }
    
    /**
     * removes a setting using ArrayList.remove
     * 
     * @param s
     *            setting to remove
     */
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    public void remove(Setting s)
    {
        Settings.remove(s);
        s.parent = null;
    }
    
    /**
     * Resets all settings for the current context.
     */
    public void resetAll()
    {
        resetAll(ModSettings.currentContext);
    }
    
    /**
     * Resets all settings for the specified context.
     * 
     * @param context
     *            The context to reset.
     */
    public void resetAll(String context)
    {
        for (int i = 0; i < Settings.size(); i++)
        {
            Settings.get(i).reset(context);
        }
    }
    
    /**
     * called every time a setting is changed saves settings file to
     * .minecraft/mods/$backendname/guiconfig.properties coming soon: set name
     * of config file
     * 
     * @param context
     *            The context to save.
     */
    @SuppressWarnings("rawtypes")
    public void save(String context)
    {
        if (!settingsLoaded)
        {
            return;
        }
        try
        {
            File path = ModSettings.getAppDir("minecraft/"
                    + ModSettings.contextDatadirs.get(context) + "/"
                    + backendname + "/");
            ModSettings.dbgout("saving context " + context + " ("
                    + path.getAbsolutePath() + " ["
                    + ModSettings.contextDatadirs.get(context) + "])");
            if (!path.exists())
            {
                path.mkdirs();
            }
            File file = new File(path, "guiconfig.properties");
            Properties p = new Properties();
            for (int i = 0; i < Settings.size(); i++)
            {
                Setting z = Settings.get(i);
                p.put(z.backendName, z.toString(context));
            }
            FileOutputStream out = new FileOutputStream(file);
            p.store(out, "");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * @return number of settings registered
     */
    public int size()
    {
        return Settings.size();
    }
}
