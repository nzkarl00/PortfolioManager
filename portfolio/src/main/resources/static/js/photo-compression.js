const MAX_WIDTH = 520;
const MAX_HEIGHT = 520;
const MIME_TYPE = "image/jpeg";
const QUALITY = 0.8;
console.log("here");

// https://labs.madisoft.it/javascript-image-compression-and-resizing/

function compress() {
    console.log("started");
    const input = document.getElementById("photo"
    const file = input.files[0];
    // Use the file
    const blobURL = window.URL.createObjectURL(file);
    const img = new Image();
    img.src = blobURL;
    img.onerror = function () {
        URL.revokeObjectURL(this.src);
        // Handle the failure properly
        console.log("Cannot load image");
    };
    img.onload = function (ev) {
        window.URL.revokeObjectURL(blobURL); // release memory
        const [newWidth, newHeight] = calculateSize(img, MAX_WIDTH, MAX_HEIGHT);
        const preview = document.getElementById('preview');
        if (preview.lastElementChild) {
            console.log("deleting");
            preview.removeChild(preview.lastElementChild);
        }

        let size = 0;
        if (img.width > img.height) {
            size = img.height;
        } else {
            size = img.width;
        }

        console.log("new");
        const canvas = document.createElement('canvas');
        preview.appendChild(canvas);
        canvas.width = newWidth;
        canvas.height = newHeight;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, size, size, 0, 0, newWidth, newHeight);
        canvas.toBlob(function (blob) {
          setFiles(input, [new File([blob], input.value.split(/(\\|\/)/g).pop())]);
        }, MIME_TYPE, QUALITY);
    };
}

function onChange(event) {
  console.log("started");
}

/**
 * Set files for an input
 * @param {HTMLInputElement} input The input [type="file"] element
 * @param {File[]} data The array of compressed files
 */
function setFiles(input, data = []) {
  // Supported only by modern browsers
  const dt = new ClipboardEvent('').clipboardData || new DataTransfer();

  for (const file of data) {
    dt.items.add(file);
  }

  if (dt.files.length) {
    input.files = dt.files;
  }
}

function calculateSize(img, maxWidth, maxHeight) {
  let width = img.width;
  let height = img.height;

  // calculate the width and height, constraining the proportions
  if (width > height) {
    if (width > maxWidth) {
      height = maxWidth;
      width = maxWidth;
    } else {
      height = width;
    }
  } else {
    if (height > maxHeight) {
      width = maxHeight;
      height = maxHeight;
    } else {
      width = height;
    }
  }
  return [width, height];
}