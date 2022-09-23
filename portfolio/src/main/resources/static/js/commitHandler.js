
commits = new Set()
existing = new Set()
removedCommits = new Set()

// move the commit referenced by the hash to the current commits container
// and change the button styling
function repositionCommit(id) {
    let commit = document.getElementById(id)
    const newContainer = document.getElementById("commit_container")
    // if the commit already exists reassign the commit variable to the old one
    if (newContainer.querySelector("#moved" + id)) {
    commit.remove()
    commit = newContainer.querySelector("#moved" + id)
    } else {
    newContainer.appendChild(commit)
    }
    // find the button to change it from an add button to a delete button
    const button = commit.querySelector(".addCommit")
    button.className = "fa fa-trash group_delete_button"
    button.innerText = ""
    button.setAttribute('onclick', "commitEditCheck();deleteCommit('" + id + "')")
    // allow for showing a commit already added in the search without overlapping the ids
    commit.id = "moved" + id
    commits.add(id + "+" + document.getElementById("commit_group").value)
    removedCommits.delete(id)
    let commitsStore = document.getElementById("commitsInput")
    let deletedCommits = document.getElementById("commitsDelete")
    commitsStore.value = Array.from(commits).join('~')
    deletedCommits.value = Array.from(removedCommits).join('~')
}

// remove the commit from the added commit list
function deleteCommit(id) {
    const commit = document.getElementById("moved" + id)
    if (commit) {
        commit.remove()
    }
    commits.forEach(comm => {
        if (comm.includes(id)) {
            commits.delete(comm)
        }
    })
    if (existing.has(id)) {
        removedCommits.add(id)
    }
    let commitsStore = document.getElementById("commitsInput")
    let deletedCommits = document.getElementById("commitsDelete")
    commitsStore.value = Array.from(commits).join('~')
    deletedCommits.value = Array.from(removedCommits).join('~')
}

// update the form property to contain all commit hashes
function storeCommits() {
    let commitsStore = document.getElementById("commitsInput")
    let deletedCommits = document.getElementById("commitsDelete")
    commitsStore.value = Array.from(commits).join('~')
    deletedCommits.value = Array.from(removedCommits).join('~')
}