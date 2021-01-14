import React from 'react'
import {useHistory} from 'react-router-dom';
import {Badge} from 'antd-mobile/lib/index';

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';

const cx = classNames.bind(styles);

const GuavaRegionMarketRow = ({page, idx, trade}) => {
    const history = useHistory();

    return (
        <div groupKey={page} key={page + '-' + idx}
             className={cx('column', trade.isActive ? '' : 'soldout')}>
            <div className={cx('row')}>
                {
                    trade.isNew ?
                        <Badge dot><span
                            className={cx(trade.isActive ? '' : 'soldout')}>{trade.date}</span></Badge> :
                        trade.date
                }
            </div>
            <div className={cx('row', 'apt_name', 'link')}
                 onClick={() => history.push('/b/' + trade.buildingId)}>{trade.name}</div>
            {/*<div className={cx('row')}>{trade.dongName}</div>*/}
            <div className={cx('row')}>
                <div>
                    {trade.area.name}
                </div>
            </div>
            <div className={cx('row')}>{trade.floor}층</div>
            <div className={cx('row', 'price')}>{trade.beforeMaxPrice == 0 ? '-' : trade.beforeMaxPriceName}</div>
            <div className={cx('row', 'price')}>
                    <span
                        className={cx(trade.isHighPrice ? 'high_price' : trade.minusPrice < 0 ? 'row_price' : '')}>{trade.priceName}</span>
                {/*<br/>*/}
                {/*<span className={cx('minus_price')}>({trade.minusPrice})</span>*/}
            </div>
        </div>
    )
};

export default GuavaRegionMarketRow;