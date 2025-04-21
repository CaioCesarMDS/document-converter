package com.caiocesarmds.documentconverter.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class NotificationUtils {
    private static final int DEFAULT_DURATION = 3;

    private static final Popup popup = new Popup();
    private static final Label popupLabel = new Label();

    static {
        popupLabel.getStyleClass().add("popup-notification");
        popup.getContent().add(popupLabel);
        popup.setAutoHide(true);
    }

    public static void showPopup(Stage stage, String message) {
        showPopup(stage, message, DEFAULT_DURATION);
    }

    public static void showPopup(Stage stage, String message, int seconds) {
        popupLabel.setText(message);

        if (!popup.isShowing()) {
            popup.show(stage);
        }

        setPopupPosition(stage);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), e -> popup.hide()));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private static void setPopupPosition(Stage stage) {
        double popupWidth = popupLabel.getWidth();
        double popupHeight = popupLabel.getHeight();

        double x = stage.getX() + (stage.getWidth() - popupWidth) / 2;
        double y = stage.getY() + (stage.getHeight() - popupHeight) / 4;

        popup.setX(x);
        popup.setY(y);
    }

}
