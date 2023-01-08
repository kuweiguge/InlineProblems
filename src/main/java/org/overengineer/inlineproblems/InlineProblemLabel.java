package org.overengineer.inlineproblems;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.util.ui.UIUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.overengineer.inlineproblems.settings.SettingsState;

import java.awt.*;


@Getter
public class InlineProblemLabel implements EditorCustomElementRenderer {

    private final String text;
    private final Color textColor;
    private final Color backgroundColor;
    private final boolean isDrawBox;
    private final boolean isRoundedCorners;

    @Setter
    private boolean isMultiLine;
    private final FontMetrics fontMetrics;
    private final Font font;

    private static final int HEIGHT_OFFSET = 2;
    private static final int WIDTH_OFFSET = 5;
    private static final int DRAW_STRING_LINE_PLACEMENT_OFFSET_Y = -1;
    private static final int DRAW_STRING_LINE_PLACEMENT_OFFSET_X = 3;

    public InlineProblemLabel(
            final String problemMessage,
            final Color textColor,
            final Color backgroundColor,
            final boolean isMultiLine,
            final boolean isDrawBox,
            final boolean isRoundedCorners,
            final Editor editor
    ) {
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.isMultiLine = isMultiLine;
        this.isDrawBox = isDrawBox;
        this.isRoundedCorners = isRoundedCorners;
        this.text = problemMessage;

        SettingsState settings = SettingsState.getInstance();

        if (settings.isUseEditorFont()) {
            font = UIUtil.getFontWithFallback(
                    editor.getColorsScheme().getFont(EditorFontType.PLAIN).getFontName(),
                    Font.PLAIN,
                    editor.getColorsScheme().getEditorFontSize()
            );
        }
        else {
            font = UIUtil.getFontWithFallback(
                    UIUtil.getToolTipFont().getFontName(),
                    Font.PLAIN,
                    editor.getColorsScheme().getEditorFontSize()
            );
        }

        fontMetrics = new Canvas().getFontMetrics(font);
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        return calcWidthInPixels();
    }

    public int calcWidthInPixels() {
        return fontMetrics.stringWidth(text) + WIDTH_OFFSET;
    }

    @Override
    public int calcHeightInPixels(@NotNull Inlay inlay) {
        return inlay.getEditor().getLineHeight();
    }

    @Override
    public void paint(@NotNull Inlay inlay, @NotNull Graphics graphics, @NotNull Rectangle targetRegion, @NotNull TextAttributes textAttributes) {
        Editor editor = inlay.getEditor();

        graphics.setFont(font);

        if (isDrawBox) {
            if (isRoundedCorners) {
                graphics.setColor(backgroundColor);

                graphics.drawRoundRect(
                        targetRegion.x,
                        targetRegion.y,
                        calcWidthInPixels(inlay),
                        calcHeightInPixels(inlay) - HEIGHT_OFFSET, // Makes the box lines visible even if line below / above is highlighted
                        5,
                        5
                );
            }
            else {
                graphics.setColor(backgroundColor);

                graphics.drawRect(
                        targetRegion.x,
                        targetRegion.y,
                        calcWidthInPixels(inlay),
                        calcHeightInPixels(inlay) - HEIGHT_OFFSET
                );
            }
        }

        graphics.setColor(textColor);
        graphics.drawString(
                text,
                targetRegion.x + DRAW_STRING_LINE_PLACEMENT_OFFSET_X,
                targetRegion.y + DRAW_STRING_LINE_PLACEMENT_OFFSET_Y + editor.getAscent()
        );
    }

    @Override
    public @Nullable GutterIconRenderer calcGutterIconRenderer(@NotNull Inlay inlay) {
        return EditorCustomElementRenderer.super.calcGutterIconRenderer(inlay);
    }
}
