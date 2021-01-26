import React from 'react'
import {useRecoilState} from 'recoil';
import {Badge} from 'antd-mobile/lib/index';

import {areaTypeState,} from '../../datatool/state';

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';

const cx = classNames.bind(styles);

const GuavaMarketRow = ({page, idx, trade}) => {
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
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
            <div className={cx('row')}>{trade.dongName}</div>
            <div className={cx('row', 'link')} onClick={() => {
                setAreaType(trade.area);
            }}>
                    <span>
                        {trade.area.name}
                    </span>
            </div>
            <div className={cx('row')}>{trade.floor}층</div>
            <div className={cx('row')}>{trade.isRent ? '있음' : '-'}</div>
            <div className={cx('row', 'price')}>{trade.beforeMaxPrice == 0 ? '-' : trade.beforeMaxPriceName}</div>
            <div className={cx('row', 'price', trade.isHighPrice ? 'high_price' : '', trade.minusPrice < 0 && 'row_price')}>{trade.priceName}{trade.subPrice > 0 && '/' + trade.subPrice}</div>
        </div>
    )
};

export default GuavaMarketRow;