package betterachievements.registry;

import betterachievements.api.IBetterAchievementPage;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.AchievementPage;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class AchievementRegistry
{
    private static AchievementRegistry instance;
    public static final AchievementPage mcPage = new AchievementPage("Minecraft");
    private List<Achievement> mcAchievements;
    private Map<String, ItemStack> iconMap;
    private boolean firstLoad;

    public static AchievementRegistry instance()
    {
        if (instance == null)
            instance = new AchievementRegistry();
        return instance;
    }

    private AchievementRegistry()
    {
        this.firstLoad = true;
        this.mcAchievements = new LinkedList<Achievement>();
        this.iconMap = new LinkedHashMap<String, ItemStack>();
    }

    private void init()
    {
        for (Object oa : AchievementList.achievementList)
        {
            Achievement achievement = (Achievement)oa;
            if (!AchievementPage.isAchievementInPages(achievement))
                mcAchievements.add(achievement);
        }
        this.iconMap.put(mcPage.getName(), new ItemStack(Blocks.grass));
        this.firstLoad = false;
    }

    public List<Achievement> getAchievements(AchievementPage page)
    {
        if (this.firstLoad) init();
        return page == mcPage ? this.mcAchievements : page.getAchievements();
    }

    public List<AchievementPage> getAllPages()
    {
        if (this.firstLoad) init();
        List<AchievementPage> pages = new LinkedList<AchievementPage>();
        pages.add(mcPage);
        pages.addAll(AchievementPage.getAchievementPages());
        return pages;
    }

    public ItemStack getItemStack(AchievementPage page)
    {
        if (page == null) return null;
        ItemStack itemStack = this.iconMap.get(page.getName());
        if (itemStack == null)
        {
            if (page instanceof IBetterAchievementPage)
                itemStack = ((IBetterAchievementPage) page).getPageIcon();
            if (itemStack == null)
            {
                for (Achievement achievement : page.getAchievements())
                {
                    if (achievement.parentAchievement == null && !achievement.getSpecial())
                    {
                        itemStack = achievement.theItemStack;
                        this.iconMap.put(page.getName(), itemStack);
                        break;
                    }
                }
            }
        }
        return itemStack;
    }
}
