var gloabalRevisionId = null;
function init() {
	tinyMCEPopup.resizeToInnerSize();
}

function saveContent() {
	if (gloabalRevisionId == null) {
		alert("Revision is not selected");
		return false;
	}
	if ( gloabalRevisionId > 0) {
		$.ajax({
			type: "GET",
			url: "/admin/article_revisions?action=details&id=" + gloabalRevisionId + "&exp=" + new Date().getTime(),
			dataType: "html",
			success: function(html, stat) {
				tinyMCE.activeEditor.setContent(html, {source_view : true});
				tinyMCEPopup.close();
		},
		error: function(html, stat) {
			alert('ERROR');
		}
		});
	}
}

function fillRevisionList() {
	$.ajax({
        type: "GET",
        url: "/admin/article_revisions?id=" + tinyMCE.article_id + "&exp=" + new Date().getTime(),
        dataType: "html",
        success: function(html, stat) {
           $("#left_select").html(html);
        },
        error: function(html, stat) { }
    });
}

function loadArticleText(el, textBlock) {
	revisionId = $(el).attr('revisionId');
	if ( revisionId > 0) {
		gloabalRevisionId = revisionId;
		$.ajax({
			type: "GET",
			url: "/admin/article_revisions?action=details&id=" + revisionId + "&exp=" + new Date().getTime(),
			dataType: "html",
			success: function(html, stat) {
			$("#" + textBlock).html(html);
		},
		error: function(html, stat) { }
		});
		displaySection(el);
	}
}

function displaySection(el) {
	$('#textTab').html($(el).find('th').html());
	$(el).parent().find('tr').each(function () {
		$(this).css('background-color', 'white');
	});
	$(el).css('background-color', '#D8E9FF');
}
