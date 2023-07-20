var editor = null
var file = null

const getLanguage = ext => {
    const languages = monaco.languages.getLanguages()
    for (let i = 0; i < languages.length; i++) {
        const { id, extensions } = languages[i]
        if (extensions) {
            if (extensions.indexOf(ext) !== -1) {
                return id
            }
        }
    }

    return getLanguage('.txt')
}

const saveFile = () => {
    const url = `/play/file?path=${file.path}`

    fetch(url, {
        method: "POST",
        body: editor.getValue()
    }).then(response => {
        if (response.ok) {
            modal.close()
        } else {
            throw new Error('save error!')
        }
    }).catch(e => {
        console.log(e)
        alert('error!')
    })
}

var modal = new tingle.modal({
    footer: false,
    stickyFooter: false,
    closeMethods: ['escape'],
    cssClass: ['custom-class', 'tingle-modal--fullscreen'],
    onOpen: function () {
        editor = monaco.editor.create(document.querySelector('.custom-class .editor'), {
            value: file.content,
            language: getLanguage(file.extension),
            automaticLayout: true
        });

        editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, function () {
            saveFile()
        });

        document.querySelector('.tingle-btn.tingle-btn--primary').addEventListener('click', () => {
            saveFile()
        })
        document.querySelector('.tingle-btn.tingle-btn--default').addEventListener('click', () => {
            modal.close()
        })
    },
    onClose: function () {
        editor?.dispose()
        editor = null
    },
    beforeClose: function () {
        return true;
    }
});

modal.setContent(`
<div class="content">
        <div class="editor"></div>
        <div class="buttons tingle-modal-box__footer">
            <button class="tingle-btn tingle-btn--primary">Save</button>
            <button class="tingle-btn tingle-btn--default">Cancel</button>
        </div>
    </div>`);

document.querySelectorAll('span').forEach(el => el.addEventListener('click', function (e) {
    const url = `/play/file?path=${el.getAttribute('data-file')}`
    fetch(url).then(response => {
        if (response.ok) {
            return response.json()
        } else {
            throw new Error("get file content error")
        }
    }).then(res => {
        file = res.data
        modal.open()
    }).catch(e => {
        console.error(e)
    })
}))