function updateCount(id, max, target) {
    const counting = document.getElementById(id).value
    document.getElementById(target).innerText = countString(max, counting)
}

function countString(max, string) {
    const count = max - string.length
    if (count == 0) { return "No characters left" }
    if (count == 1) { return "1 character left" }
    return count + " characters left"
}