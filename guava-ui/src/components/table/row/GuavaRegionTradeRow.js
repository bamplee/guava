import React from 'react'
import {useHistory} from 'react-router-dom';
import {useRecoilValue} from 'recoil';
import {Badge} from 'antd-mobile/lib/index';

import {regionState} from '../../datatool/state';

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';

const cx = classNames.bind(styles);

const GuavaRegionTradeRow = ({page, idx, trade}) => {
    const history = useHistory();

    // const [region, setRegion] = useRecoilState(regionState);
    const region = useRecoilValue(regionState);

    return (
        <div groupKey={page} key={page + '-' + idx}
             className={cx('column')}>
            <div className={cx('row')}>
                {
                    trade.isNew ?
                        <Badge dot>{
                            region.type !== 'DONG' ?
                                trade.month + '.' + trade.day :
                                trade.date
                        }</Badge> :
                        region.type !== 'DONG' ?
                            trade.month + '.' + trade.day :
                            trade.date
                }
            </div>
            {
                region.type !== 'DONG' &&
                <div className={cx('row', 'link')}
                     onClick={() => history.push('/r/' + trade.regionId)}>{trade.dongName}</div>
            }
            <div className={cx('row', 'link', 'apt_name')}
                 onClick={() => {
                     history.push('/b/' + trade.buildingId);
                 }}>
                {trade.name}
            </div>
            <div className={cx('row')}>
                <div>{trade.area.name}</div>
            </div>
            <div className={cx('row')}>{trade.floor}층</div>
            <div className={cx('row', 'price', trade.isHighPrice ? 'high_price' : '')}>{trade.price}</div>
        </div>
    )
};

export default GuavaRegionTradeRow;