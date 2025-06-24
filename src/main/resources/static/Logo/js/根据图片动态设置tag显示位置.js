function setAddTagPosition(img, addTag, fallbackLeft = 8, padding = 80) {
    const container = img.parentNode;
    const containerWidth = container.offsetWidth || 160;
    const containerHeight = container.offsetHeight || 190;

    const nw = img.naturalWidth;
    const nh = img.naturalHeight;

    const ratio = Math.min(containerWidth / nw, containerHeight / nh);
    const renderedWidth = nw * ratio;

    if (renderedWidth < containerWidth) {
        addTag.style.left = `${renderedWidth - padding}px`;
    } else {
        addTag.style.left = `${fallbackLeft}px`;
    }
}

img.onload = function () {
    setAddTagPosition(img, addTag);
};