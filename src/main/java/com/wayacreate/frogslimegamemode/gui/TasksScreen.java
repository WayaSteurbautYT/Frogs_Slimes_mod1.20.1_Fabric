package com.wayacreate.frogslimegamemode.gui;

import com.wayacreate.frogslimegamemode.tasks.TaskType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TasksScreen extends Screen {
    private static final int TITLE_COLOR = 0xFFFFFF;
    private static final int TEXT_COLOR = 0xE0E0E0;

    public TasksScreen() {
        super(Text.literal("Tasks"));
    }

    @Override
    protected void init() {
        int buttonWidth = 120;
        int buttonHeight = 20;
        int x = (this.width - buttonWidth) / 2;
        int y = this.height - 28;

        this.addDrawableChild(
            ButtonWidget.builder(Text.literal("Close"), button -> this.close())
                .dimensions(x, y, buttonWidth, buttonHeight)
                .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, TITLE_COLOR);

        int startY = 50;
        int lineHeight = 12;
        int index = 0;
        for (TaskType task : TaskType.values()) {
            String line = task.name() + " (/" + task.getRequiredAmount() + ")";
            context.drawTextWithShadow(this.textRenderer, line, 20, startY + (index * lineHeight), TEXT_COLOR);
            index++;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

