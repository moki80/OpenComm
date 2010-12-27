function oc(a)
{
	var o = {};
	for(var i=0;i<a.length;i++)
	{
		o[a[i]]='';
	}
	return o;
}
function removeFromArray(object, array) {
	var idx = array.indexOf(object); // Find the index
	if(idx!=-1) array.splice(idx, 1); // Remove it if really found!
	return array;
}
function user(name,img,id,color) {
this.name=name;
this.img=img;
this.id=id;
this.color=color;
this.html=$('<div class="icon" style="background:'+color+'"><span class="name">'+this.name+'</span><img src="'+this.img+'" alt="'+this.img+'" /></div>');
}

$(document).ready(function(){
center();
var yui = new user("Yui", 'img/yui.png', '',"#ff77ff;");
var otonashi = new user("Otonashi", 'img/otonashi.png', '',"#ff8877;");
var hinata = new user("Hinata", 'img/hinata.png', '',"#7788ff;");
yui.html.css("left",100);
yui.html.css("top",50);
otonashi.html.css("left",200);
otonashi.html.css("top",100);
hinata.html.css("left",300);
hinata.html.css("top",100);
$("#centerspace").append(yui.html);
$("#centerspace").append(otonashi.html);
$("#centerspace").append(hinata.html);
$(".icon").each(function(){$(this).draggable();});
$("#lefttab").droppable({
      drop: function(a,z) { z.draggable.css("top",100);z.draggable.css("left",100);$("#leftspace").append(z.draggable); }
    });
$("#leftcentertab").droppable({
      drop: function(a,z) { z.draggable.css("top",100);z.draggable.css("left",100);$("#centerspace").append(z.draggable); }
    });
$("#righttab").droppable({
      drop: function(a,z) { z.draggable.css("top",100);z.draggable.css("left",100);$("#rightspace").append(z.draggable); }
    });
$("#rightcentertab").droppable({
      drop: function(a,z) { z.draggable.css("top",100);z.draggable.css("left",100);$("#centerspace").append(z.draggable); }
      });
$("#lefttab").click(function() {
    left();
    });
$("#righttab").click(function() {
    right();
    });
$("#leftcentertab").click(function() {
    center();
    });
$("#rightcentertab").click(function() {
    center();
    });
});
function left() {
$("#leftspace").animate({left:0},1000);
$("#centerspace").animate({left:800},1000);
}
function center() {
$("#rightspace").animate({left:800},1000);
$("#centerspace").animate({left:0},1000);$("#leftspace").animate({left:-800},1000);
}
function right() {
$("#rightspace").animate({left:0},1000);
$("#centerspace").animate({left:-800},1000);
}