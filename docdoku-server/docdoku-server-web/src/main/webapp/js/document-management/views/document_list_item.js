define([
	"i18n!localization/nls/document-management-strings",
	"common/date",
	"views/checkbox_list_item",
    "views/iteration/document_iteration",
	"text!templates/document_list_item.html"
], function (
	i18n,
	date,
	CheckboxListItemView,
    IterationView,
	template
) {
	var DocumentListItemView = CheckboxListItemView.extend({

		template: Mustache.compile(template),

		tagName: "tr",

		initialize: function () {
			CheckboxListItemView.prototype.initialize.apply(this, arguments);
			this.events["click .reference"] = this.actionEdit;
			this.events["click .state-subscription"] = this.toggleStateSubscription;
			this.events["click .iteration-subscription"] = this.toggleIterationSubscription;
		},

		modelToJSON: function () {
			var data = this.model.toJSON();
			if (this.model.hasIterations()) {
				data.lastIteration = this.model.getLastIteration().toJSON();
                data.lastIteration.creationDate = date.formatTimestamp(
                    i18n._DATE_FORMAT,
                    data.lastIteration.creationDate
                );
			}

            if (this.model.isCheckout()) {
                data.checkOutDate = date.formatTimestamp(
                    i18n._DATE_FORMAT,
                    data.checkOutDate
                );
            }

            data.isCheckoutByConnectedUser = this.model.isCheckoutByConnectedUser();
            data.isCheckout = this.model.isCheckout();

			return data;
		},

        rendered: function() {
            CheckboxListItemView.prototype.rendered.apply(this, arguments);

            if(this.model.isStateChangedSubscribed()){
                this.$(".state-subscription").addClass("icon-bell-alt").attr("title",i18n.UNSUBSCRIBE_STATE_CHANGE);
            }else{
                this.$(".state-subscription").addClass("icon-bell").attr("title",i18n.SUBSCRIBE_STATE_CHANGE);
            }

            if(this.model.isIterationChangedSubscribed()){
                this.$(".iteration-subscription").addClass("icon-bell-alt").attr("title",i18n.UNSUBSCRIBE_ITERATION_CHANGE);
            }else{
                this.$(".iteration-subscription").addClass("icon-bell").attr("title",i18n.SUBSCRIBE_ITERATION_CHANGE);
            }

            this.$(".author-popover").userPopover(this.model.attributes.author.login, this.model.id, "left");

            if(this.model.isCheckout()) {
                this.$(".checkout-user-popover").userPopover(this.model.getCheckoutUser().login, this.model.id, "left");
            }

        },

		actionEdit: function (evt) {
			var that = this;
			this.model.fetch().success(function () {
                new IterationView({
                    model: that.model
                }).show();
            });
        },

        toggleStateSubscription:function(evt){
            this.model.toggleStateSubscribe(this.model.isStateChangedSubscribed());
        },

        toggleIterationSubscription:function(evt){
            this.model.toggleIterationSubscribe(this.model.isIterationChangedSubscribed());
        }

    });

    return DocumentListItemView;
});
