/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
Wtf.ns('Wtf.ux.data');
Wtf.ux.data.PagingStore = Wtf.extend(Wtf.data.Store, {
    destroy: function() {
        if (this.storeId || this.id) {
            Wtf.StoreMgr.unregister(this);
        }
        delete this.data;
        delete this.allData;
        delete this.snapshot;
        this.purgeListeners();
    },
    add: function(records) {
        records = [].concat(records);
        if (records.length < 1) {
            return;
        }
        for (var i = 0, len = records.length; i < len; i++) {
            records[i].join(this);
        }
        var index = this.data.length;
        this.data.addAll(records);
        if (this.allData) {
            this.allData.addAll(records);
        }
        if (this.snapshot) {
            this.snapshot.addAll(records);
        }
        this.fireEvent("add", this, records, index);
    },
    remove: function(record) {
        var index = this.data.indexOf(record);
        this.data.removeAt(index);
        if (this.allData) {
            this.allData.remove(record);
        }
        if (this.snapshot) {
            this.snapshot.remove(record);
        }
        if (this.pruneModifiedRecords) {
            this.modified.remove(record);
        }
        this.fireEvent("remove", this, record, index);
    },
    removeAll: function() {
        this.data.clear();
        if (this.allData) {
            this.allData.clear();
        }
        if (this.snapshot) {
            this.snapshot.clear();
        }
        if (this.pruneModifiedRecords) {
            this.modified = [];
        }
        this.fireEvent("clear", this);
    },
    insert: function(index, records) {
        records = [].concat(records);
        for (var i = 0, len = records.length; i < len; i++) {
            this.data.insert(index, records[i]);
            records[i].join(this);
        }
        if (this.allData) {
            this.allData.addAll(records);
        }
        if (this.snapshot) {
            this.snapshot.addAll(records);
        }
        this.fireEvent("add", this, records, index);
    },
    getById: function(id) {
        return (this.snapshot || this.allData || this.data).key(id);
    },
    load: function(options) {
        options = options || {};
        if (this.fireEvent("beforeload", this, options) !== false) {
            this.storeOptions(options);
            var p = Wtf.apply({}, options.params, this.baseParams);
            if (this.sortInfo && this.remoteSort) {
                var pn = this.paramNames;
                p[pn["sort"]] = this.sortInfo.field;
                p[pn["dir"]] = this.sortInfo.direction;
            }
            if (this.isPaging(p)) { (function() {
                    if (this.allData) {
                        this.data = this.allData;
                        delete this.allData;
                    }
                    this.applyPaging();
                    this.fireEvent("datachanged", this);
                    var r = [].concat(this.data.items);
			        this.fireEvent("load", this, r, options);
			        if (options.callback) {
			            options.callback.call(options.scope || this, r, options, true);
			        }
                }).defer(1, this);
                return true;
            }
            this.proxy.load(p, this.reader, this.loadRecords, this, options);
            return true;
        } else {
            return false;
        }
    },
    loadRecords: function(o, options, success) {
        if (!o || success === false) {
            if (success !== false) {
                this.fireEvent("load", this, [], options);
            }
            if (options.callback) {
                options.callback.call(options.scope || this, [], options, false);
            }
            return;
        }
        var r = o.records, t = o.totalRecords || r.length;
        if (!options || options.add !== true) {
            if (this.pruneModifiedRecords) {
                this.modified = [];
            }
            for (var i = 0, len = r.length; i < len; i++) {
                r[i].join(this);
            }
            if (this.allData) {
                this.data = this.allData;
                delete this.allData;
            }
            if (this.snapshot) {
                this.data = this.snapshot;
                delete this.snapshot;
            }
            this.data.clear();
            this.data.addAll(r);
            this.totalLength = t;
            this.applySort();
            if (!this.allData) {
                this.applyPaging();
            }
            if (r.length != this.getCount()) {
                r = [].concat(this.data.items);
            }
            this.fireEvent("datachanged", this);
        } else {
            this.totalLength = Math.max(t, this.data.length + r.length);
            this.add(r);
        }
        this.fireEvent("load", this, r, options);
        if (options.callback) {
            options.callback.call(options.scope || this, r, options, true);
        }
    },
    loadData: function(o, append) {
        this.isPaging(Wtf.apply({}, this.lastOptions ? this.lastOptions.params : null, this.baseParams));
        var r = this.reader.readRecords(o);
        this.loadRecords(r, {add: append}, true);
    },
    getTotalCount: function() {
        return this.allData ? this.allData.getCount() : this.totalLength || 0;
    },
    sortData: function(f, direction) {
        direction = direction || 'ASC';
        var st = this.fields.get(f).sortType;
        var fn = function(r1, r2) {
            var v1 = st(r1.data[f]), v2 = st(r2.data[f]);
            return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
        };
        if (this.allData) {
            this.data = this.allData;
            delete this.allData;
        }
        this.data.sort(direction, fn);
        if (this.snapshot && this.snapshot != this.data) {
            this.snapshot.sort(direction, fn);
        }
        this.applyPaging();
    },
    filterBy: function(fn, scope) {
        this.snapshot = this.snapshot || this.allData || this.data;
        delete this.allData;
        this.data = this.queryBy(fn, scope || this);
        this.applyPaging();
        this.fireEvent("datachanged", this);
    },
    queryBy: function(fn, scope) {
        var data = this.snapshot || this.allData || this.data;
        return data.filterBy(fn, scope || this);
    },
    collect: function(dataIndex, allowNull, bypassFilter) {
        var d = (bypassFilter === true ? this.snapshot || this.allData || this.data: this.data).items;
        var v, sv, r = [], l = {};
        for (var i = 0, len = d.length; i < len; i++) {
            v = d[i].data[dataIndex];
            sv = String(v);
            if ((allowNull || !Wtf.isEmpty(v)) && !l[sv]) {
                l[sv] = true;
                r[r.length] = v;
            }
        }
        return r;
    },
    clearFilter: function(suppressEvent) {
        if (this.isFiltered()) {
            this.data = this.snapshot;
            delete this.allData;
            delete this.snapshot;
            this.applyPaging();
            if (suppressEvent !== true) {
                this.fireEvent("datachanged", this);
            }
        }
    },
    isFiltered: function() {
        return this.snapshot && this.snapshot != (this.allData || this.data);
    },
    isPaging: function(params) {
        var pn = this.paramNames, start = params[pn.start], limit = params[pn.limit];
        if ((typeof start != 'number') || (typeof limit != 'number')) {
            delete this.start;
            delete this.limit;
            this.lastParams = params;
            return false;
        }
        this.start = start;
        this.limit = limit;
        delete params[pn.start];
        delete params[pn.limit];
        var lastParams = this.lastParams;
        this.lastParams = params;
        if (!this.proxy) {
            return true;
        }
        if (!lastParams) {
            return false;
        }
        for (var param in params) {
            if (params.hasOwnProperty(param) && (params[param] !== lastParams[param])) {
                return false;
            }
        }
        for (param in lastParams) {
            if (lastParams.hasOwnProperty(param) && (params[param] !== lastParams[param])) {
                return false;
            }
        }
        return true;
    },
    applyPaging: function() {
        var start = this.start, limit = this.limit;
        if ((typeof start == 'number') && (typeof limit == 'number')) {
            var allData = this.data, data = new Wtf.util.MixedCollection(allData.allowFunctions, allData.getKey);
            data.items = allData.items.slice(start, start + limit);
            data.keys = allData.keys.slice(start, start + limit);
            var len = data.length = data.items.length;
            var map = {};
            for (var i = 0; i < len; i++) {
                var item = data.items[i];
                map[data.getKey(item)] = item;
            }
            data.map = map;
            this.allData = allData;
            this.data = data;
        }
    }
});
/* 
*
*Sagar A - Not in used
*/
/*Wtf.ux.data.SimplePagingStore = function(config) {
    Wtf.ux.data.SimplePagingStore.superclass.constructor.call(this, Wtf.apply(config, {
        reader: new Wtf.data.ArrayReader(config)
    }));
};
Wtf.extend(Wtf.ux.data.SimplePagingStore, Wtf.ux.data.PagingStore, {
    loadData: function(data, append) {
        if (this.expandData === true) {
            var r = [];
            for (var i = 0, len = data.length; i < len; i++) {
                r[r.length] = [data[i]];
            }
            data = r;
        }
        Wtf.ux.data.SimplePagingStore.superclass.loadData.call(this, data, append);
    }
});
*/
Wtf.ux.data.JsonPagingStore = function(config) {
    Wtf.ux.data.JsonPagingStore.superclass.constructor.call(this, Wtf.apply(config, {
        reader: new Wtf.data.JsonReader(config)
    }));
};
Wtf.extend(Wtf.ux.data.JsonPagingStore, Wtf.ux.data.PagingStore);
