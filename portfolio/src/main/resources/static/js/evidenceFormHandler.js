
dateTooltip = '<span id="info" class="tooltip_body_evidence">This is the date the evidence occurred, date selection is restricted to the boundaries of the project it is assigned to.</span>'

$("#evidence_date_tool").tooltip({
    title: dateTooltip,
    placement: "top",
    trigger: "hover",
    container: "body",
    html: true
});

categoryOptionsShown = true;
selectedCategories = new Set()

function storeCategories() {
    let categoryStore = document.getElementById("category_hidden")
    const categoriesToSave = Array.from(selectedCategories).join('~');
    categoryStore.value = categoriesToSave
}

function showCategories() {
    let categoryOptions = document.getElementById("category_multiselect_items")
    if (categoryOptionsShown) {
        categoryOptions.style.display = "block"
        categoryOptionsShown = false
    } else {
        categoryOptions.style.display = "none"
        categoryOptionsShown = true
    }
}

function updateCategories(event) {
    let newCategory = event.value
    if (selectedCategories.has(newCategory)) {
        selectedCategories.delete(newCategory)
    } else {
        selectedCategories.add(event.value)
    }
    let categoryDisplay = document.getElementById("evidence_category")
    if (selectedCategories.size == 0) {
        categoryDisplay.innerText = "Select Categories"
    } else {
        categoryDisplay.innerText = [...selectedCategories].join(', ')
    }
    storeCategories()
}

document.addEventListener("click", function (e) {
    let categoryOptions = document.getElementById("category_multiselect_items")
    if (e.target != document.getElementById("evidence_category") && e.target.className != "category_item" && categoryOptions != null) {
        categoryOptions.style.display = "none"
        categoryOptionsShown = true
    }
});

// Event listener to add skills when enter is pressed and the skill input is selected
document.querySelector("#add_skill_input").addEventListener("keyup", event => {
    if (event.key !== "Enter" && event.key !== " ") return;
    addSkill()
})

//Returns the skills
function storeSkills() {
    let skillStore = document.getElementById("skillHidden")
    const skillList = Array.from(skills).join('~');
    skillStore.value = skillList
}

skills = new Set()
skillRow = document.getElementById("skill_sub_1");

// Adds the skill to the set and resets the input.
function addSkill() {
    let newSkill = document.getElementById("add_skill_input").value
    if (newSkill.slice(-1) === " ") {
        newSkill = newSkill.slice(0, -1);
    }

    if (skills.has(newSkill)) {
        document.getElementById("add_skill_input").value = "";
        return;
    }
    // check to see if it matches the correct skill format
    const validate = newSkill.match(/^[a-zA-Z-_0-9]+$/)
    if (!validate || !validate.length == 1) {
        document.getElementById("skill_error").style = "color:red;";
        return
    }
    skills.add(newSkill)
    if (allSkills !== null) {
        allSkills = allSkills.filter(s => s !== newSkill)
        autocomplete(document.getElementById("add_skill_input"), allSkills)
    }
    document.getElementById("add_skill_input").value = ""
    appendSkill(newSkill)
}

// Inserts a new skill tag into the form
function appendSkill(skillText) {
    let skillTag = document.createElement("button");
    skillTag.id = "skill_" + skillText
    skillTag.className = "table_text skill_tag"
    skillTag.innerText = skillText + " ✖"
    skillTag.value = skillText
    skillTag.onclick = removeSkill;
    skillRow.appendChild(skillTag);
    if (skillRow.getBoundingClientRect().right < skillTag.getBoundingClientRect().right) {
        skillRow.removeChild(skillTag)
        insertSkillRow()
        skillRow.appendChild(skillTag);
    }
    storeSkills()
}

/* Creates a new row for skill tags to be placed on, for some reason inserting <br> couldn't do this*/
function insertSkillRow() {
    let newSkillRow = document.createElement("div");
    newSkillRow.style.display = "flex"
    document.getElementById("skills_container").appendChild(newSkillRow)
    skillRow = newSkillRow
}

// Deletes a skill then rebuilds the skill list
function removeSkill(event) {
    skills.delete(event.target.value)
    allSkills.push(event.target.value)
    autocomplete(document.getElementById("add_skill_input"), allSkills)
    clearSkills()
    insertSkillRow()
    for (const skill of skills) {
        appendSkill(skill)
    }
    return false;
}

// Removes all skills from display
function clearSkills() {
    let skillContainer = document.getElementById("skills_container")
    while (skillContainer.lastChild) {
        skillContainer.removeChild(skillContainer.lastChild)
    }
}

// Event listener to add links when enter is pressed and the link input is selected
document.querySelector("#add_link_input").addEventListener("keyup", event => {
    if (event.key !== "Enter") return;
    addLink()
})

links = new Set();
linksContainer = document.getElementById("links_container")
linkCounter = 0;

// Adds the link to the set and resets the input.
function addLink() {
    let newLink = document.getElementById("add_link_input").value
    if (newLink === "") {
        return;
    }
    if (!validateLink(newLink)) {
        alert('Link is not valid, please ensure link includes http(s):// protocol');
        return;
    }
    if (links.has(newLink)) return;
    links.add(newLink)
    document.getElementById("add_link_input").value = ""
    appendLink(newLink)
}

// Validate a link
function validateLink(link) {
    let pattern = /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi;
    return pattern.test(link);
}

// Inserts a new link into the form
function appendLink(linkText) {
    let linkBox = document.createElement("div");
    linksContainer.appendChild(linkBox)
    linkBox.className = "trim_text"
    linkBox.style.display = "flex"
    let newLink = document.createElement("a");
    newLink.id = "link_" + linkText
    newLink.className = "sprint-description display_data text_button trim_text"
    newLink.style.fontSize = "110%"
    newLink.width = "90% !important"
    newLink.style.display = "inline-block"
    newLink.target = "_blank"
    linkCounter++
    newLink.innerText = linkCounter + ".  " + linkText
    newLink.href = linkText
    let linkDelete = document.createElement("button");
    linkDelete.className = "fa fa-trash each_link"
    linkDelete.value = linkText
    linkDelete.onclick = removeLink;
    linkBox.appendChild(newLink)
    linkBox.appendChild(linkDelete)
    storeLinks()
}

// Store the javascript set of links into a hidden HTML divider,
// which can be retrieved in Java from the linkInput ID
// This is to transfer the frontend set of links into the backend.
function storeLinks() {
    let linksInput = document.getElementById("linksInput")
    const linkList = Array.from(links).join(' ');
    linksInput.value = linkList
}

// Deletes a link then rebuilds the link list
function removeLink(event) {
    links.delete(event.target.value)
    clearLinks()
    linkCounter = 0
    for (const link of links) {
        appendLink(link)
    }
    return false;
}

// Removes all links from display
function clearLinks() {
    let linkContainer = document.getElementById("links_container")
    while (linkContainer.lastChild) {
        linkContainer.removeChild(linkContainer.lastChild)
    }
}

function updateTitle() {
    const titleCounter = document.getElementById("titleCharCount")
    const titleInput = document.getElementById("evidence_title")
    titleCounter.innerText = "Characters Remaining: "  + (100 - titleInput.value.length)
}

function updateDesc() {
    const descCounter = document.getElementById("descCharCount")
    const descInput = document.getElementById("evidence_desc")
    descCounter.innerText = "Characters Remaining: "  + (2000 - descInput.value.length)
}

function updateSkills() {
    const skillCounter = document.getElementById("skillCharCount")
    const skillInput = document.getElementById("add_skill_input")
    skillCounter.innerText = "Characters Remaining: "  + (100 - skillInput.value.length)
}

// Enable form and hide add evidence form
function displayAddEvidenceForm() {
    document.getElementById('evidence_form').style = "display: block";
    document.getElementById('add_button').style = "display: none";
}

// Enable add evidence button and hide form
function displayAddButton() {
    document.getElementById('evidence_form').style = "display: none";
    document.getElementById('add_button').style = "display: block";
}
