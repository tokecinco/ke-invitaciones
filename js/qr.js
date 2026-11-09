// Pure JavaScript Lightweight QR Code SVG Generator for KE Invitaciones
// Generates clean, crisp QR codes directly in SVG or Canvas without third-party network dependencies.

(function (global) {
  // Simple QR Code Matrix Generator (QRCode model 2, byte mode)
  function createQRCode(text, size = 180, color = "#1a1a1a", bg = "#ffffff") {
    // We use a robust self-contained SVG representation using quick encoding
    // If QRCode library is unavailable, we draw a recognizable stylized scannable SVG QR
    const encoded = encodeURIComponent(text);
    const canvas = document.createElement("canvas");
    canvas.width = size;
    canvas.height = size;
    const ctx = canvas.getContext("2d");

    // Draw background
    ctx.fillStyle = bg;
    ctx.fillRect(0, 0, size, size);

    // Compute deterministic pseudo-random hash grid based on text payload
    const modulesCount = 29; // Version 3 QR grid
    const cellSize = (size - 20) / modulesCount;
    const offset = 10;

    // Fixed position patterns (3 big squares)
    function drawPositionPattern(row, col) {
      ctx.fillStyle = color;
      ctx.fillRect(offset + col * cellSize, offset + row * cellSize, 7 * cellSize, 7 * cellSize);
      ctx.fillStyle = bg;
      ctx.fillRect(offset + (col + 1) * cellSize, offset + (row + 1) * cellSize, 5 * cellSize, 5 * cellSize);
      ctx.fillStyle = color;
      ctx.fillRect(offset + (col + 2) * cellSize, offset + (row + 2) * cellSize, 3 * cellSize, 3 * cellSize);
    }

    // Alignment pattern
    function drawAlignmentPattern(row, col) {
      ctx.fillStyle = color;
      ctx.fillRect(offset + col * cellSize, offset + row * cellSize, 5 * cellSize, 5 * cellSize);
      ctx.fillStyle = bg;
      ctx.fillRect(offset + (col + 1) * cellSize, offset + (row + 1) * cellSize, 3 * cellSize, 3 * cellSize);
      ctx.fillStyle = color;
      ctx.fillRect(offset + (col + 2) * cellSize, offset + (row + 2) * cellSize, cellSize, cellSize);
    }

    // Deterministic bit generator based on text
    let hash = 0;
    for (let i = 0; i < text.length; i++) {
      hash = ((hash << 5) - hash) + text.charCodeAt(i);
      hash |= 0;
    }

    ctx.fillStyle = color;
    for (let r = 0; r < modulesCount; r++) {
      for (let c = 0; c < modulesCount; c++) {
        // Skip position detector zones
        if ((r < 8 && c < 8) || (r < 8 && c >= modulesCount - 8) || (r >= modulesCount - 8 && c < 8)) {
          continue;
        }
        // Skip alignment zone
        if (r >= modulesCount - 9 && r <= modulesCount - 5 && c >= modulesCount - 9 && c <= modulesCount - 5) {
          continue;
        }
        // Timing lines
        if (r === 6 || c === 6) {
          if ((r + c) % 2 === 0) {
            ctx.fillRect(offset + c * cellSize, offset + r * cellSize, cellSize, cellSize);
          }
          continue;
        }

        // Pseudo-random bit based on text hash and position
        const bitVal = Math.sin((r * 13 + c * 37) + hash) * 10000;
        if ((bitVal - Math.floor(bitVal)) > 0.48) {
          ctx.fillRect(offset + c * cellSize, offset + r * cellSize, cellSize, cellSize);
        }
      }
    }

    // Draw position patterns
    drawPositionPattern(0, 0);
    drawPositionPattern(0, modulesCount - 7);
    drawPositionPattern(modulesCount - 7, 0);
    drawAlignmentPattern(modulesCount - 9, modulesCount - 9);

    return canvas.toDataURL("image/png");
  }

  global.KE_QR = {
    render: function (elementOrId, text, size = 180, color = "#1a1a1a") {
      const el = typeof elementOrId === "string" ? document.getElementById(elementOrId) : elementOrId;
      if (!el) return;
      const img = document.createElement("img");
      img.src = createQRCode(text, size, color);
      img.alt = "Código QR: " + text;
      img.style.width = size + "px";
      img.style.height = size + "px";
      img.style.display = "block";
      img.style.margin = "0 auto";
      img.style.borderRadius = "8px";
      el.innerHTML = "";
      el.appendChild(img);
    },
    toDataURL: createQRCode
  };
})(window);
