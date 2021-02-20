import React from 'react'
import {useRecoilState} from 'recoil';
import {Button, Modal, Range} from 'antd-mobile';

import {filterAreaState, showAreaFilterState} from '../../../datatool/state';

import classNames from 'classnames/bind';
import styles from './areaRangeModal.module.scss';

const cx = classNames.bind(styles);

const AreaRangeModal = () => {
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const [showAreaFilter, setShowAreaFilter] = useRecoilState(showAreaFilterState);

    const onClickAreaRange = (val1, val2) => {
        if (filterArea[0] === 0 && filterArea[1] === 5) {
            setFilterArea([val1, val2]);
            return;
        }
        if (filterArea[0] === val1 && filterArea[1] === val2) {
            return;
        }
        if (filterArea[0] === val1 && filterArea[1] >= val2) {
            setFilterArea([val2, filterArea[1]]);
            return;
        }
        if (filterArea[0] < val1 && filterArea[1] >= val2) {
            setFilterArea([filterArea[0], val1]);
            return;
        }
        let data = Array.from(new Set([val1, val2, filterArea[0], filterArea[1]])).sort();
        setFilterArea([data[0], data[data.length - 1]]);
    };

    return (
        <Modal
            popup
            maskClosable={true}
            visible={showAreaFilter}
            onCancel={() => setShowAreaFilter(false)}
            onClose={() => setShowAreaFilter(false)}
            animationType="slide-up"
        >
            <div className={cx('area_container')}>
                <div className={cx('area_range')}>
                    <Range
                        style={{marginLeft: 30, marginRight: 30}}
                        step={1}
                        min={0}
                        max={5}
                        marks={{1: '50㎡', 2: '60㎡', 3: '85㎡', 4: '95㎡', 5: '105㎡'}}
                        value={filterArea}
                        onChange={(e) => {
                            if (e[0] + e[1] > 0 && !(e[0] === e[1])) {
                                setFilterArea(e)
                            }
                        }}
                        onAfterChange={(e) => {
                        }}
                    />
                </div>
                <div className={cx('area_radio')}>
                    <Button type={filterArea[0] === 0 && filterArea[1] === 5 ? 'primary' : 'ghost'} inline
                            size="small"
                            onClick={(e) => onClickAreaRange(0, 5)}
                    >전체</Button>
                    <Button type={filterArea[0] === 0 && filterArea[1] >= 1 ? 'primary' : 'ghost'} inline
                            size="small"
                            onClick={(e) => onClickAreaRange(0, 1)}
                    >0&#13217;~50&#13217;</Button>
                    <Button type={filterArea[0] <= 1 && filterArea[1] >= 2 ? 'primary' : 'ghost'} inline
                            size="small"
                            onClick={(e) => onClickAreaRange(1, 2)}
                    >50&#13217;~60&#13217;</Button>
                    <Button type={filterArea[0] <= 2 && filterArea[1] >= 3 ? 'primary' : 'ghost'} inline
                            size="small"
                            onClick={(e) => onClickAreaRange(2, 3)}
                    >60&#13217;~85&#13217;</Button>
                    <Button type={filterArea[0] <= 3 && filterArea[1] >= 4 ? 'primary' : 'ghost'} inline
                            size="small"
                            onClick={(e) => onClickAreaRange(3, 4)}
                    >85&#13217;~105&#13217;</Button>
                    <Button type={filterArea[0] <= 4 && filterArea[1] >= 5 ? 'primary' : 'ghost'} inline
                            size="small"
                            onClick={(e) => onClickAreaRange(4, 5)}
                    >105&#13217;~</Button>
                </div>
            </div>
        </Modal>
    )
};


export default AreaRangeModal;