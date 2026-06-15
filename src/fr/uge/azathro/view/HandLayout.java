package fr.uge.azathro.view;

record HandLayout(int screenWidth, int screenHeight) {
    private static final double CARD_WIDTH_RATIO = 0.09d;
    private static final double BOTTOM_MARGIN_RATIO = 0.05d;
    private static final double GAP_RATIO = 0.012d;

    int cardWidth() {
        return (int) (screenWidth * CARD_WIDTH_RATIO);
    }

    int cardHeight() {
        return (int) (cardWidth() * 1.5d);
    }

    int gap() {
        return (int) (screenWidth * GAP_RATIO);
    }

    int bottomMargin() {
        return (int) (screenHeight * BOTTOM_MARGIN_RATIO);
    }

    int totalHandWidth(int handSize) {
        return handSize * cardWidth() + Math.max(0, handSize - 1) * gap();
    }

    int handXOrigin(int handSize) {
        return (screenWidth - totalHandWidth(handSize)) / 2;
    }

    int handYOrigin() {
        return screenHeight - bottomMargin() - cardHeight();
    }

    int cardIndexFromX(float x, int handSize) {
        if (handSize <= 0) {
            return -1;
        }
        var handOriginX = handXOrigin(handSize);
        if (x < handOriginX || x > handOriginX + totalHandWidth(handSize)) {
            return -1;
        }
        var cardIndex = (int) ((x - handOriginX) / (cardWidth() + gap()));
        return Math.clamp(cardIndex, 0, handSize - 1);
    }

    boolean isInsideCardArea(float x, float y, int handSize) {
        if (handSize <= 0) {
            return false;
        }
        var handOriginX = handXOrigin(handSize);
        var handOriginY = handYOrigin();
        return x >= handOriginX && x <= handOriginX + totalHandWidth(handSize)
                && y >= handOriginY && y <= handOriginY + cardHeight();
    }
}
