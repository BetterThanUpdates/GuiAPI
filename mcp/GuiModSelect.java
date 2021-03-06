package net.minecraft.src;

/**
 * This is a Subclass of GuiModScreen, and acts as the entry point from the
 * button in the options menu. This is just used internally.
 * 
 * @author lahwran
 */
public class GuiModSelect extends GuiModScreen
{
    @SuppressWarnings("unused")
    private static void selectScreen(Integer i)
    {
        GuiModScreen.show(ModSettingScreen.modScreens.get(i).theWidget);
        GuiModScreen.clicksound();
    }
    
    protected GuiModSelect(GuiScreen screen)
    {
        super(screen);
        WidgetClassicTwocolumn w = new WidgetClassicTwocolumn();
        w.verticalPadding = 10;
        for (int i = 0; i < ModSettingScreen.modScreens.size(); i++)
        {
            ModSettingScreen m = ModSettingScreen.modScreens.get(i);
            w.add(GuiApiHelper.makeButton(m.buttonTitle, "selectScreen",
                    GuiModSelect.class, false, new Class[]
                    { Integer.class }, i));
        }
        WidgetSimplewindow mainwidget = new WidgetSimplewindow(w,
                "Select a Mod");
        mainwidget.hPadding = 0;
        mainwidget.mainWidget.setTheme("scrollpane-notch");
        this.mainwidget = mainwidget;
    }
}
