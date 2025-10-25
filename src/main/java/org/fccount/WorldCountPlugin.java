package org.fccount;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.FriendsChatManager;
import net.runelite.api.FriendsChatMember;
import net.runelite.api.events.FriendsChatChanged;
import net.runelite.api.events.FriendsChatMemberJoined;
import net.runelite.api.events.FriendsChatMemberLeft;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
        name = "FC World Populations",
        description = "Counts FC members per world and shows top 3 popular worlds.",
        tags = {"fc", "friendschat", "world", "counter", "population"}
)
@Slf4j
public class WorldCountPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WorldCountOverlay overlay;

    @Inject
    private net.runelite.client.callback.ClientThread clientThread;

    private final Map<Integer, Integer> worldCounts = new HashMap<>();
    private int tickCounter = 0;

    @Override
    protected void startUp()
    {
        log.info("WorldCountPlugin started");
        overlayManager.add(overlay);
        updateCounts();
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        worldCounts.clear();
    }

    @Subscribe
    public void onFriendsChatChanged(FriendsChatChanged event)
    {
        clientThread.invokeLater(this::updateCounts);
    }

    @Subscribe
    public void onFriendsChatMemberJoined(FriendsChatMemberJoined event)
    {
        updateCounts();
    }

    @Subscribe
    public void onFriendsChatMemberLeft(FriendsChatMemberLeft event)
    {
        updateCounts();
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        tickCounter++;
        if (tickCounter >= 10)
        {
            tickCounter = 0;
            updateCounts();
        }
    }

    private void updateCounts()
    {
        worldCounts.clear();
        FriendsChatManager fc = client.getFriendsChatManager();
        if (fc == null || fc.getMembers() == null)
            return;

        for (FriendsChatMember member : fc.getMembers())
        {
            if (member == null) continue;
            int world = member.getWorld();
            if (world == 0) continue;
            worldCounts.put(world, worldCounts.getOrDefault(world, 0) + 1);
        }

        overlay.setWorldCounts(worldCounts);
    }
}
