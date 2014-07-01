package com.simplicityapks.reminderdatepicker.lib;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Serves as Adapter for all PickerSpinner Views and deals with an extra footer and its layout, the
 * option to add temporary selections and correctly setting the view for the TwinTextItems.
 */
public class PickerSpinnerAdapter extends ArrayAdapter<TwinTextItem>{

    // IDs for both TextViews:
    private static final int PRIMARY_TEXT_ID = android.R.id.text1;
    private static final int SECONDARY_TEXT_ID = android.R.id.text2;

    private final int itemResource;
    /**
     * Resource for the last item in the Spinner, which will be inflated at the last position in dropdown/dialog.
     * Set to 0 for use of normal dropDownResource
     */
    private int footerResource = 0;

    /**
     * Temporary item which is selected immediately and not shown in the dropdown menu or dialog.
     * That is why it does not increase getCount().
     */
    private TwinTextItem temporarySelection;

    /**
     * The last item, set to null to disable
     */
    private TwinTextItem footer;

    private final LayoutInflater inflater;

    /**
     * Constructs a new PickerSpinnerAdapter with these params:
     * @param context The context needed by any Adapter.
     * @param resource The resource to be inflated as layout, should contain two TextViews
     * @param footerResource The resource to be inflated for the footer.
     */
    public PickerSpinnerAdapter(Context context, int resource, int footerResource) {
        super(context, resource, PRIMARY_TEXT_ID);
        this.itemResource = resource;
        this.footerResource = footerResource;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * Constructs a new PickerSpinnerAdapter with these params:
     * @param context The context needed by any Adapter.
     * @param resource The resource to be inflated as layout, should contain two TextViews
     * @param items The TwinTextItems to be shown in layout.
     * @param footerResource The resource to be inflated for the footer.
     * @param footer The item to be shown as footer, use TwinTextItem.Simple for easy creation.
     */
    public PickerSpinnerAdapter(Context context, int resource, List<TwinTextItem> items,
                                int footerResource, TwinTextItem footer) {
        super(context, resource, PRIMARY_TEXT_ID, items);
        this.itemResource = resource;
        this.footerResource = footerResource;
        this.footer = footer;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(itemResource, parent, false);
        } else {
            view = convertView;
        }
        if(temporarySelection != null && position == getCount()) {
            // our inflated view acts as temporaryView:
            return setTextsAndCheck(view, temporarySelection);
        } else {
            // we have a normal item, set the texts:
            return setTextsAndCheck(view, getItem(position));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // depending on the position, use super method or create our own
        // we don't need to inflate a footer view if it uses the default resource, the superclass will do it:
        if(footer == null || footerResource == 0 || position != getCount()-1) {
            // we have a normal item or a footer with same resource
            return setTextsAndCheck(inflater.inflate(itemResource, parent, false), getItem(position));
        } else {
            // if we want the footer, create it:
            return setTextsAndCheck(inflater.inflate(footerResource, parent, false), footer);
        }
    }

    private View setTextsAndCheck(View view, TwinTextItem item) {
        if (view == null) throw new IllegalArgumentException(
                "The resource passed to constructor or setItemResource()/setFooterResource() is invalid");
        final TextView primaryText = (TextView) view.findViewById(PRIMARY_TEXT_ID);
        if (primaryText == null) throw new IllegalArgumentException(
                "The resource passed to constructor or setItemResource()/setFooterResource() does not " +
                        "contain a textview with id set to android.R.id.text1"
        );
        primaryText.setText(item.getPrimaryText());
        final TextView secondaryText = (TextView) view.findViewById(SECONDARY_TEXT_ID);
        if (secondaryText != null)
            secondaryText.setText(item.getSecondaryText());
        return view;
    }

    /**
     * Push an item to be selected, but not shown in the dropdown menu. This is similar to calling
     * setText(item.toString()) if a Spinner had such a method.
     * @param item The item to select, or null to remove any temporary selection.
     */
    public void selectTemporary(TwinTextItem item) {
        this.temporarySelection = item;
        notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TwinTextItem getItem(int position) {
        if(temporarySelection != null && position == getCount())
            return temporarySelection;
        else if(footer != null && position == getCount()-1)
            return footer;
        else
            return super.getItem(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        // we need one extra item which is not in the array.
        return super.getCount() + (footer==null? 0 : 1);
    }

    /**
     * Sets the text to be shown in the footer.
     * @param footer An Object whose toString() will be the footer text, or null to disable the footer.
     */
    public void setFooter(TwinTextItem footer) {
        this.footer = footer;
    }

    /**
     * Sets the layout resource to be inflated as footer. It should contain a TextView with id set
     * to android.R.id.text1, where the text will be added.
     * @param footerResource A valid xml layout resource, or 0 to use dropDownResource instead.
     */
    public void setFooterResource(int footerResource) {
        this.footerResource = footerResource;
    }
}
