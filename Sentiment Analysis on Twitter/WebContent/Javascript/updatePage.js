document.addEventListener('DOMContentLoaded', function () {
  var searchDivs = document.querySelectorAll('#testDiv');
  for (var i = 0; i < searchDivs.length; i++) {
    searchDivs[0].addEventListener('click', searcher);
  }
});
function searcher()
{	
	$(document).ready(function()
	{
		$('#testDiv').append("Appended");
	});
}
