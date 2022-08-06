/* reference https://www.w3schools.com/howto/howto_js_autocomplete.asp*/
function autocomplete(inp, arr) {

    let currentItem;

    inp.addEventListener("input", function(e) {
        let value = this.value
        closeAllLists()
        if (!value) { return false}
        currentItem = -1

        // Create autocomplete option container
        let optionContainer = document.createElement("div");
        optionContainer.setAttribute("id", "skills_autocomplete_container")
        optionContainer.setAttribute("class", "autocomplete-items")
        this.parentNode.appendChild(optionContainer)

        // Add possible autocomplete options to the container
        for (let i = 0; i < arr.length; i++) {
            if (arr[i].substr(0, value.length).toUpperCase() == value.toUpperCase()) {
                let optionItem = document.createElement("div")
                optionItem.innerHTML = "<strong>" + arr[i].substr(0, value.length) + "</strong>"
                optionItem.innerHTML += arr[i].substr(value.length)
                optionItem.value = arr[i]
                optionItem.addEventListener("click", function(e) {
                    inp.value = optionItem.value
                    addSkill()
                    updateSkills()
                    closeAllLists()
                });
                optionContainer.appendChild(optionItem)
            }
        }
    });

    // Mapping for down, up and enter keys to navigate the list of autocomplete options.
    inp.addEventListener("keydown", function(e) {
        let optionContainer = document.getElementById("skills_autocomplete_container")
        if (optionContainer) {
            optionContainer = optionContainer.getElementsByTagName("div")
        }
        if (e.keyCode === 40) { // DOWN
            currentItem++
            addActive(optionContainer)
        } else if (e.keyCode === 38) { // UP
            currentItem--
            addActive(optionContainer)
        } else if (e.keyCode === 13) { // ENTER
            e.preventDefault()
            if (currentItem > -1) {
                if (optionContainer) optionContainer[currentItem].click()
            }
            closeAllLists()
        }
    });

    // Sets an autocomplete list item to be active
    function addActive(x) {
        if (!x) return false
        removeActive(x)
        if (currentItem >= x.length) currentItem = 0
        if (currentItem < 0) currentItem = (x.length - 1)
        x[currentItem].classList.add("autocomplete-active")
    }

    // Deactivates all active autocomplete items
    function removeActive(x) {
        for (let i = 0; i < x.length; i++) {
            x[i].classList.remove("autocomplete-active")
        }
    }

    // Closes autocomplete lists that aren't the parameter
    function closeAllLists(activeInput) {
        let x = document.getElementsByClassName("autocomplete-items")
        for (let i = 0; i < x.length; i++) {
            if (activeInput != x[i] && activeInput != inp) {
                x[i].parentNode.removeChild(x[i])
            }
        }
    }

    // When the user clicks anywhere, close all autocompletes that are not selected
    document.addEventListener("click", function (e) {
        closeAllLists(e.target)
    });

    return false;
}