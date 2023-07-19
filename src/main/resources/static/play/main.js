var editor = null
var modal = new tingle.modal({
    footer: true,
    stickyFooter: false,
    closeMethods: ['overlay', 'button', 'escape'],
    closeLabel: "Close",
    cssClass: ['custom-class'],
    onOpen: function () {
        editor = monaco.editor.create(document.querySelector('.custom-class .editor'), {
            value: ['function x() {', '\tconsole.log("Hello world!");', '}'].join('\n'),
            language: 'javascript'
        });
    },
    onClose: function () {
        editor.dispose()
        editor = null
    },
    beforeClose: function () {
        return true;
    }
});

modal.setContent('<div class="editor"></div>');

// add a button
modal.addFooterBtn('Save', 'tingle-btn tingle-btn--primary tingle-btn--pull-right', function () {
    // here goes some logic
    modal.close();
});

function showModal() {
    // open modal
    modal.open();

}