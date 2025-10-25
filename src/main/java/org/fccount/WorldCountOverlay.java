package org.fccount;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.api.FriendsChatManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class WorldCountOverlay extends Overlay
{
    private final Client client;
    private final PanelComponent panel = new PanelComponent();
    private Map<Integer, Integer> worldCounts;

    @Inject
    public WorldCountOverlay(Client client)
    {
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);

        //panel.setBackgroundColor(new Color(0, 0, 0, 150));
        //panel.setBorder(new Rectangle(0, 0, 0, 0)); // optional: remove default border
    }

    public void setWorldCounts(Map<Integer, Integer> worldCounts)
    {
        this.worldCounts = worldCounts;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (worldCounts == null || worldCounts.isEmpty())
            return null;

        // Set panel properties for proper sizing
        panel.setPreferredSize(new Dimension(165, 0));
        // Ensure proper spacing between components
        panel.setGap(new Point(0, 4));

        panel.getChildren().clear();
        panel.setBackgroundColor(new Color(18, 18, 18, 180)); // Dark background

        // Get Friends Chat manager
        FriendsChatManager fc = client.getFriendsChatManager();
        String fcName = (fc != null) ? fc.getName() : "No Friends Chat";

        // Add title
        panel.getChildren().add(TitleComponent.builder()
                //.text("Friends Chat Population:")
                .text("FC World Populations:")
                .color(Color.WHITE)
                .build());

        int minPlayers = 0; // hard-coded threshold

        // Add top 3 worlds
        worldCounts.entrySet().stream()
                .filter(entry -> entry.getValue() >= minPlayers)
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(3)
                .forEach(entry -> panel.getChildren().add(LineComponent.builder()
                                .left("World " + entry.getKey())
                                .right(entry.getValue() + " members")
                                .leftColor(Color.WHITE)
                                .rightColor(Color.GREEN)
                                .build()
                ));

        // Padding
        //panel.getChildren().add(LineComponent.builder().build());
        //panel.getChildren().add(LineComponent.builder()
                //.left(" ")
                //.build());

        // Keep main panel in vertical orientation for the rest of the overlay
        panel.setOrientation(ComponentOrientation.VERTICAL);

        return panel.render(graphics);
    }
}
