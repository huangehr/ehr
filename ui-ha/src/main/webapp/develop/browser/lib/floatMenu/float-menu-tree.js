(function ($,win) {
    var _el = undefined;
    var options = {
        data : undefined,
        url : undefined,
        queryParams: {},
        ajaxType:'get',
        idField: undefined,
        parentIdField: undefined,
        isFloat : false,
        top : '120px',
        load: true,
        target: undefined,
        afterClick: undefined,
        contextPath: undefined,
        loadCallFunc: undefined
    }

    function arrayToTree (data, id, pid){
        var g = this, p = options;
        var childrenName = "children";
        if (!data || !data.length) return [];
        var targetData = [];                    //存储数据的容器(返回)
        var records = {};
        var itemLength = data.length;           //数据集合的个数
        for (var i = 0; i < itemLength; i++)
        {
            var o = data[i];
            var key = getKey(o[id]);
            records[key] = o;
        }
        for (var i = 0; i < itemLength; i++)
        {
            var currentData = data[i];
            var key = getKey(currentData[pid]);
            var parentData = records[key];
            if (!parentData)
            {
                targetData.push(currentData);
                continue;
            }
            parentData[childrenName] = parentData[childrenName] || [];
            parentData[childrenName].push(currentData);
        }
        return targetData;

        function getKey(key)
        {
            if (typeof (key) == "string") key = key.replace(/[.]/g, '').toLowerCase();
            return key;
        }
    }

    function load(opts){
        opts = $.extend(options, opts||{});
        if(opts.url){
            $.ajax({
                type: opts.ajaxType,
                url: opts.url,
                data: opts.queryParams,
                dataType:"json",
                success: function(data){
                    debugger
                    if(opts.loadCallFunc)
                        data = opts.loadCallFunc(data);
                    options.data = data;
                    render();
                },
                error : function () {
                    alert('加载出错！');
                }
            });
        }
        else
            render();
    }

    function render(){
        var html =  getMenus();
        $(_el).empty();
        $(_el).append(html.join(''));
        setFloat();
        bindEvent();
    }

    function getMenus(){
        var menus = [];
        menus.push(
            '<div class="float-menu-tree">',
                '<div id="floatMenu" class="float-menu">',
                    '<div class="float-menu-detail">',
                        '<ul class="tree-root">'
        )
        var html = '';
        var data = options.data;
        if(options.idField && options.parentIdField){
            data = arrayToTree(data, options.idField, options.parentIdField);
        }
        if(!$.isArray(data))
            return;
        for(var i=0; i < data.length; i++) {
            html = '';
            html +=
                '<li id="lm_'+i+'" class="s-bc18 c-mt2 '+ (i==0?'lm-active':'') +'" style="cursor:pointer;">'    +
                '<div class="li-ico"></div>' +
                '<div class="title">'+data[i]['text']+'</div>' +
                '</li>' +
                '<ul style="cursor:point; '+ (i==0?'':'display:none') +'">';
            if($.isArray(data[i].children)){
                for(var j=0; j<data[i].children.length; j++){
                    html +=
                        '<li id="lm-'+i+'-'+j+'">' +
                        '<a href="javascript:void(0)" class="'+ (i==0 && j==0?'active' : '') +'">' +
                        '<div class="li-a-ico"></div>' +
                        '<span>' + data[i].children[j].text + '</span>' +
                        '</a>'  +
                        '</li>';
                }
            }
            html += '</ul>';
            menus.push(html);
        }
        menus.push(
                    '</ul>' ,
                '</div>',
                '<div class="expand-btn">',
                    '<div id="expandBtn" class="active"></div>',
                '</div>',
            '</div>',
            '</div>'
        )
        return menus;
    }

    function setFloat(opts){
        opts = $.extend(options, opts||{});
        var el = $(_el).find('.float-menu-tree');
        if(opts.isFloat && el){
            el.css({'position':'fixed', 'top': opts.top || '120px'});
        }
        else
            el.css({'position':'absolute', 'top': opts.top || '120px'});
    }

    function setTarget(target){
        var opts = $.extend(options, {target:target});
        if(!opts.target instanceof jQuery){
            opts.target = $('#'+opts.target);
        }
    }

    function bindEvent(){
        var p = _el, opts = options;
        var treeId = $(p).attr('id');
        setTarget();
        //绑定菜单点击事件
        $.each($('#'+treeId+' a'), function (i, el) {
            $(el).click(function (event) {
                $.each($(p).find('.tree-root a[class^="active"]'), function (i, el) {
                    $(el).removeClass('active');
                });
                $(this).addClass('active');
                var ids = $(this).parent().attr('id').split('-');
                var data = opts.data[ids[1]].children[ids[2]];
                opts.target.empty();
                opts.target.append('<div class="r-loading">&nbsp;</div>');
                if(opts.load){
                    opts.target.load(opts.contextPath + data.url, data.parms);
                }
                else{
                    opts.target.attr('src', data.url);
                }
                if(opts.afterClick)
                    opts.afterClick(this, data);
            })
        });

        $.each($('#'+treeId).find('li[id^="lm_"]'), function (i, el) {
            var child = $(el).next();
            $(el).click(function () {
                if($(this).attr('class').indexOf('lm-active')==-1){
                    $.each($('#'+treeId).find('li[class*="lm-active"]'), function (i, el) {
                        $(el).removeClass('lm-active');
                        $(el).next().slideUp('fast');
                    })
                    $(child).slideDown('fast');
                    $(this).addClass('lm-active');
                }
            })
            //debugger
        })
        //绑定收缩展开事件
        var btn = $('#'+treeId+' div[id="expandBtn"]');
        if(btn){
            btn.click(function (event) {
                var el = this;
                var floatMenuTree = $('#'+treeId+' div[class="float-menu-tree"]');
                var floatMenu = $('#'+treeId+' div[id="floatMenu"]');
                if($(el).attr('class').indexOf('active')>-1){
                    $(floatMenu).css('left','-240px');
                    $(floatMenuTree).css('width','20px');
                    $(el).removeClass('active');
                }
                else{
                    $(floatMenu).css('left','0px');
                    $(floatMenuTree).css('width','260px');
                    $(el).addClass('active');
                }

            })
            btn.hover(
                function () {
                    $(this).addClass('over');
                },
                function () {
                    $(this).removeClass('over');
                }
            )
        }


        function scroll(el, w, l, diff){
            var left = $(el).css('')
        }
    }



    $.fn.floatMenuTree = function (opts) {
        _el = this;
        load(opts);
    };
})(jQuery, window);